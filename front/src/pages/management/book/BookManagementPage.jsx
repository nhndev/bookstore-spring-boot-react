import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { useFormik } from 'formik';
import { Button, Card, Form, Modal, Table } from 'react-bootstrap';
import { FaBook, FaInbox, FaPlus, FaSearch } from 'react-icons/fa';
import { useDispatch, useSelector } from 'react-redux';
import * as Yup from 'yup';

import MoneyField from '../../../components/ui/MoneyField';
import MultiImageUpload from '../../../components/ui/MultiImageUpload';
import '../../../components/ui/MultiImageUpload/MultiImageUpload.scss';
import Pagination from '../../../components/ui/Pagination';
import Select from '../../../components/ui/Select';
import SortableTh from '../../../components/ui/SortableTh';
import TableAction from '../../../components/ui/TableAction';
import TextField from '../../../components/ui/TextField';
import { BOOK } from '../../../constants/label';
import { PAGINATION } from '../../../constants/setting';
import { setLoading } from '../../../redux/slice/app.slice';
import { showConfirmDialog } from '../../../redux/slice/confirmDialog.slice';
import authorService from '../../../services/author.service';
import bookService from '../../../services/book.service';
import categoryService from '../../../services/category.service';
import publisherService from '../../../services/publisher.service';
import dateUtil from '../../../utils/date.util';
import formatUtil from '../../../utils/format.util';
import messageUtil from '../../../utils/message.util';
import validationUtil from '../../../utils/validation.util';
import '../management-common.scss';

function BookManagementPage() {
  const isLoading = useSelector((state) => state.app.isLoading);
  const [searchCondition, setSearchCondition] = useState(null);
  const [data, setData] = useState([]);
  const [totalItems, setTotalItems] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [sortBy, setSortBy] = useState(null);
  const [sortOrder, setSortOrder] = useState('asc');
  const dispatch = useDispatch();

  const totalPages = useMemo(() => {
    return Math.ceil(totalItems / PAGINATION.PAGE_SIZE);
  }, [totalItems]);

  const fetchData = useCallback(
    async (page) => {
      try {
        dispatch(setLoading(true));
        const res = await bookService.search({
          pageNum: page,
          pageSize: PAGINATION.PAGE_SIZE,
          name: searchCondition?.name || undefined,
          categoryId: searchCondition?.categoryId || undefined,
          publisherId: searchCondition?.publisherId || undefined,
          sortBy: sortBy || undefined,
          sortOrder: sortOrder || undefined,
        });
        setData(res.data ?? []);
        setTotalItems(res.totalItems ?? 0);
      } catch {
        setData([]);
        setTotalItems(0);
      } finally {
        dispatch(setLoading(false));
      }
    },
    [searchCondition, sortBy, sortOrder, dispatch],
  );

  const handleSort = useCallback(
    (field) => {
      setPageNum(1);
      setSortBy(field);
      setSortOrder((order) =>
        sortBy === field ? (order === 'asc' ? 'desc' : 'asc') : 'asc',
      );
    },
    [sortBy],
  );

  useEffect(() => {
    fetchData(pageNum);
  }, [pageNum, searchCondition, fetchData]);

  const handleChangePage = useCallback((page) => {
    setPageNum(page);
  }, []);

  const onClickEditBtn = () => {
    // TODO: open edit modal or navigate to edit page
    messageUtil.showSuccessMessage('Chức năng đang phát triển');
  };

  const onClickDeleteBtn = (item) => {
    dispatch(
      showConfirmDialog({
        message: messageUtil.getDeleteConfirmationMsg(
          `xoá <strong>Sách ${item.name}</strong>`,
        ),
        okFunc: () => deleteRecord(item.id),
      }),
    );
  };

  const deleteRecord = async (id) => {
    try {
      dispatch(setLoading(true));
      await bookService.delete(id);
      messageUtil.showSuccessMessage();
      fetchData(1);
    } catch {
      // Error page or toast shown by httpClient
    } finally {
      dispatch(setLoading(false));
    }
  };

  const [searchName, setSearchName] = useState('');
  const [searchCategoryId, setSearchCategoryId] = useState('');
  const [searchPublisherId, setSearchPublisherId] = useState('');
  const [categories, setCategories] = useState([]);
  const [publishers, setPublishers] = useState([]);
  const [authors, setAuthors] = useState([]);
  const [showAddModal, setShowAddModal] = useState(false);

  useEffect(() => {
    const loadOptions = async () => {
      try {
        const [catRes, pubRes, authorRes] = await Promise.all([
          categoryService.findAllChildCategories(),
          publisherService.search({ pageNum: 1, pageSize: 500 }),
          authorService.search({ pageNum: 1, pageSize: 500 }),
        ]);
        setCategories(catRes?.data ?? []);
        setPublishers(pubRes?.data ?? []);
        setAuthors(authorRes?.data ?? []);
      } catch {
        // ignore
      }
    };
    loadOptions();
  }, []);

  const categoryOptions = useMemo(
    () => categories.map((c) => ({ key: c.id, value: c.name })),
    [categories],
  );
  const publisherOptions = useMemo(
    () => publishers.map((p) => ({ key: p.id, value: p.name })),
    [publishers],
  );
  const authorOptions = useMemo(
    () => authors.map((a) => ({ key: a.id, value: a.name })),
    [authors],
  );

  const createFormik = useFormik({
    initialValues: {
      name: '',
      description: '',
      price: '',
      discount: '',
      quantity: '',
      publishYear: '',
      language: '',
      layout: '',
      pageCount: '',
      size: '',
      weight: '',
      categoryId: '',
      publisherId: '',
      authorIds: [],
      mainAuthorId: '',
      imageFiles: [],
    },
    enableReinitialize: true,
    validateOnChange: false,
    validateOnBlur: true,
    validationSchema: Yup.object({
      name: Yup.string()
        .required(validationUtil.requiredMsg(BOOK.NAME))
        .max(255, validationUtil.maxLength(BOOK.NAME, 255)),
      price: Yup.string().test('money', function (value) {
        const msg = validationUtil.validateMoney(value, {
          required: true,
          min: 0,
          max: 999999999999,
          maxLength: 12,
          label: BOOK.PRICE,
        });
        return msg ? this.createError({ message: msg }) : true;
      }),
      discount: Yup.number()
        .required(validationUtil.requiredMsg(BOOK.DISCOUNT))
        .min(0, 'Giảm giá phải từ 0-100')
        .max(100, 'Giảm giá phải từ 0-100'),
      quantity: Yup.number()
        .required(validationUtil.requiredMsg(BOOK.QUANTITY))
        .integer('Số lượng phải là số nguyên')
        .min(0, 'Số lượng phải lớn hơn hoặc bằng 0'),
      categoryId: Yup.string().required('Danh mục là trường bắt buộc'),
      publisherId: Yup.string().required('Nhà xuất bản là trường bắt buộc'),
      authorIds: Yup.array()
        .min(1, 'Chọn ít nhất một tác giả')
        .required('Tác giả là trường bắt buộc'),
      layout: Yup.string().max(255, validationUtil.maxLength(BOOK.LAYOUT, 255)),
      pageCount: Yup.number()
        .integer('Số trang phải là số nguyên')
        .min(0, 'Số trang phải lớn hơn hoặc bằng 0')
        .nullable()
        .transform((v) => (v === '' || Number.isNaN(v) ? null : v)),
      size: Yup.string().max(255, validationUtil.maxLength(BOOK.SIZE, 255)),
      weight: Yup.number()
        .integer('Khối lượng phải là số nguyên')
        .min(0, 'Khối lượng phải lớn hơn hoặc bằng 0')
        .nullable()
        .transform((v) => (v === '' || Number.isNaN(v) ? null : v)),
      imageFiles: Yup.array().min(1, 'Chọn ít nhất 1 ảnh'),
    }),
    onSubmit: async () => {
      const {
        name,
        description,
        price,
        discount,
        quantity,
        publishYear,
        language,
        layout,
        pageCount,
        size,
        weight,
        categoryId,
        publisherId,
        authorIds,
        mainAuthorId,
        imageFiles,
      } = createFormik.values;
      const authorIdList = Array.isArray(authorIds)
        ? authorIds
        : [authorIds].filter(Boolean);
      const mainId = mainAuthorId || authorIdList[0];
      const authorsPayload = authorIdList.map((authorId) => ({
        authorId,
        isMain: authorId === mainId,
      }));
      const bookPayload = {
        name: name.trim(),
        description: description?.trim() || null,
        price: Number(price),
        discount: Number(discount),
        quantity: Number(quantity),
        publishYear: publishYear ? Number(publishYear) : null,
        language: language?.trim() || null,
        layout: layout?.trim() || null,
        pageCount:
          pageCount !== '' && !Number.isNaN(Number(pageCount))
            ? Number(pageCount)
            : null,
        size: size?.trim() || null,
        weight:
          weight !== '' && !Number.isNaN(Number(weight))
            ? Number(weight)
            : null,
        categoryId,
        publisherId,
        authors: authorsPayload,
      };
      const formData = new FormData();
      formData.append(
        'book',
        new Blob([JSON.stringify(bookPayload)], { type: 'application/json' }),
      );
      const filesList = Array.isArray(imageFiles)
        ? imageFiles
        : imageFiles
          ? Array.from(imageFiles)
          : [];
      if (filesList.length) {
        for (let i = 0; i < filesList.length; i++) {
          formData.append('files', filesList[i]);
        }
      }
      try {
        dispatch(setLoading(true));
        await bookService.create(formData, {
          headers: { 'Content-Type': undefined },
        });
        messageUtil.showSuccessMessage();
        createFormik.resetForm();
        setShowAddModal(false);
        fetchData(1);
      } catch {
        // Error page or toast shown by httpClient
      } finally {
        dispatch(setLoading(false));
      }
    },
  });

  const handleAuthorSelect = (e) => {
    const options = e.target.options;
    const selected = [];
    for (let i = 0; i < options.length; i++) {
      if (options[i].selected) selected.push(options[i].value);
    }
    createFormik.setFieldValue('authorIds', selected);
    createFormik.setFieldTouched('authorIds', true);
    if (!selected.includes(createFormik.values.mainAuthorId)) {
      createFormik.setFieldValue('mainAuthorId', selected[0] || '');
    }
  };

  const handleCloseAddModal = () => {
    createFormik.resetForm();
    setShowAddModal(false);
  };

  const onSearch = (e) => {
    e.preventDefault();
    const name = searchName.trim() || null;
    const categoryId = searchCategoryId || null;
    const publisherId = searchPublisherId || null;
    const hasFilter = name || categoryId || publisherId;
    setSearchCondition(hasFilter ? { name, categoryId, publisherId } : null);
    setPageNum(1);
  };

  const onResetSearch = (e) => {
    e.preventDefault();
    setSearchCondition(null);
    setSearchName('');
    setSearchCategoryId('');
    setSearchPublisherId('');
    setPageNum(1);
  };

  return (
    <div className="management-page book-management">
      {/* Add Book Modal */}
      <Modal
        centered
        show={showAddModal}
        onHide={handleCloseAddModal}
        size="xl"
      >
        <Modal.Header closeButton className="border-0 pb-0">
          <Modal.Title className="d-flex align-items-center gap-2 fs-5">
            <span className="modal-icon bg-success rounded-2 d-flex align-items-center justify-content-center text-white">
              <FaPlus />
            </span>
            Thêm sách mới
          </Modal.Title>
        </Modal.Header>
        <Form onSubmit={createFormik.handleSubmit}>
          <Modal.Body className="pt-3">
            <div className="row g-3">
              <div className="col-12">
                <TextField
                  label={BOOK.NAME}
                  name="name"
                  maxLength={255}
                  value={createFormik.values.name}
                  touched={createFormik.touched.name}
                  handleChange={createFormik.handleChange}
                  handleBlur={createFormik.handleBlur}
                  error={createFormik.errors.name}
                  placeholder="Nhập tên sách"
                  required
                />
              </div>
              <div className="col-12">
                <Form.Group>
                  <Form.Label>Mô tả</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={2}
                    name="description"
                    value={createFormik.values.description}
                    onChange={createFormik.handleChange}
                    onBlur={createFormik.handleBlur}
                    placeholder="Mô tả (tuỳ chọn)"
                  />
                </Form.Group>
              </div>
              <div className="col-md-4">
                <MoneyField
                  label={BOOK.PRICE}
                  name="price"
                  value={createFormik.values.price}
                  touched={createFormik.touched.price}
                  handleChange={createFormik.handleChange}
                  handleBlur={createFormik.handleBlur}
                  error={createFormik.errors.price}
                  placeholder="0"
                  required
                  min={0}
                  max={999999999999}
                  maxLength={12}
                  setFieldError={createFormik.setFieldError}
                />
              </div>
              <div className="col-md-4">
                <TextField
                  label={BOOK.DISCOUNT}
                  name="discount"
                  value={createFormik.values.discount}
                  touched={createFormik.touched.discount}
                  handleChange={createFormik.handleChange}
                  handleBlur={createFormik.handleBlur}
                  error={createFormik.errors.discount}
                  placeholder="0"
                  required
                />
              </div>
              <div className="col-md-4">
                <TextField
                  label={BOOK.QUANTITY}
                  name="quantity"
                  value={createFormik.values.quantity}
                  touched={createFormik.touched.quantity}
                  handleChange={createFormik.handleChange}
                  handleBlur={createFormik.handleBlur}
                  error={createFormik.errors.quantity}
                  placeholder="0"
                  required
                />
              </div>
              <div className="col-md-6">
                <Select
                  label="Danh mục"
                  name="categoryId"
                  value={createFormik.values.categoryId}
                  handleChange={createFormik.handleChange}
                  handleBlur={createFormik.handleBlur}
                  error={createFormik.errors.categoryId}
                  touched={createFormik.touched.categoryId}
                  required
                  options={[
                    { key: '', value: '-- Chọn danh mục --' },
                    ...categoryOptions,
                  ]}
                />
              </div>
              <div className="col-md-6">
                <Select
                  label="Nhà xuất bản"
                  name="publisherId"
                  value={createFormik.values.publisherId}
                  handleChange={createFormik.handleChange}
                  handleBlur={createFormik.handleBlur}
                  error={createFormik.errors.publisherId}
                  touched={createFormik.touched.publisherId}
                  required
                  options={[
                    { key: '', value: '-- Chọn nhà xuất bản --' },
                    ...publisherOptions,
                  ]}
                />
              </div>
              <div className="col-md-6">
                <Form.Group>
                  <Form.Label>
                    Tác giả (chọn nhiều)
                    <span className="text-danger ms-1">*</span>
                  </Form.Label>
                  <Form.Select
                    multiple
                    name="authorIds"
                    value={createFormik.values.authorIds}
                    onChange={handleAuthorSelect}
                    onBlur={() =>
                      createFormik.setFieldTouched('authorIds', true)
                    }
                    className={
                      (createFormik.touched.authorIds ||
                        createFormik.submitCount > 0) &&
                      createFormik.errors.authorIds
                        ? 'is-invalid'
                        : ''
                    }
                  >
                    {authorOptions.map((opt) => (
                      <option key={opt.key} value={opt.key}>
                        {opt.value}
                      </option>
                    ))}
                  </Form.Select>
                  <Form.Text className="text-muted small">
                    Giữ Ctrl (Windows) hoặc Cmd (Mac) để chọn nhiều
                  </Form.Text>
                  {(createFormik.touched.authorIds ||
                    createFormik.submitCount > 0) &&
                    createFormik.errors.authorIds && (
                      <div className="invalid-feedback d-block">
                        {createFormik.errors.authorIds}
                      </div>
                    )}
                </Form.Group>
              </div>
              <div className="col-md-6">
                <Select
                  label="Tác giả chính"
                  name="mainAuthorId"
                  value={createFormik.values.mainAuthorId}
                  handleChange={createFormik.handleChange}
                  handleBlur={createFormik.handleBlur}
                  options={[
                    { key: '', value: '-- Chọn tác giả chính --' },
                    ...(createFormik.values.authorIds?.length
                      ? authorOptions.filter((o) =>
                          createFormik.values.authorIds.includes(o.key),
                        )
                      : []),
                  ]}
                />
              </div>
              <div className="col-md-6">
                <Form.Group>
                  <Form.Label>Năm xuất bản (tuỳ chọn)</Form.Label>
                  <Form.Control
                    type="number"
                    name="publishYear"
                    value={createFormik.values.publishYear}
                    onChange={createFormik.handleChange}
                    onBlur={createFormik.handleBlur}
                    placeholder="VD: 2024"
                  />
                </Form.Group>
              </div>
              <div className="col-md-6">
                <Form.Group>
                  <Form.Label>Ngôn ngữ (tuỳ chọn)</Form.Label>
                  <Form.Control
                    type="text"
                    name="language"
                    maxLength={255}
                    value={createFormik.values.language}
                    onChange={createFormik.handleChange}
                    onBlur={createFormik.handleBlur}
                    placeholder="VD: Tiếng Việt"
                  />
                </Form.Group>
              </div>
              <div className="col-md-6">
                <TextField
                  label={BOOK.LAYOUT}
                  name="layout"
                  maxLength={255}
                  value={createFormik.values.layout}
                  touched={createFormik.touched.layout}
                  handleChange={createFormik.handleChange}
                  handleBlur={createFormik.handleBlur}
                  error={createFormik.errors.layout}
                  placeholder="VD: Bìa Mềm"
                />
              </div>
              <div className="col-md-6">
                <TextField
                  label={BOOK.PAGE_COUNT}
                  name="pageCount"
                  value={createFormik.values.pageCount}
                  touched={createFormik.touched.pageCount}
                  handleChange={createFormik.handleChange}
                  handleBlur={createFormik.handleBlur}
                  error={createFormik.errors.pageCount}
                  placeholder="VD: 300"
                />
              </div>
              <div className="col-md-6">
                <TextField
                  label={BOOK.SIZE}
                  name="size"
                  maxLength={255}
                  value={createFormik.values.size}
                  touched={createFormik.touched.size}
                  handleChange={createFormik.handleChange}
                  handleBlur={createFormik.handleBlur}
                  error={createFormik.errors.size}
                  placeholder="VD: 24 x 17 cm"
                />
              </div>
              <div className="col-md-6">
                <TextField
                  label={BOOK.WEIGHT}
                  name="weight"
                  value={createFormik.values.weight}
                  touched={createFormik.touched.weight}
                  handleChange={createFormik.handleChange}
                  handleBlur={createFormik.handleBlur}
                  error={createFormik.errors.weight}
                  placeholder="VD: 350 (gram)"
                />
              </div>
              <div className="col-12">
                <Form.Group>
                  <MultiImageUpload
                    label={
                      <>
                        Ảnh sách <span className="text-danger">*</span>
                      </>
                    }
                    value={createFormik.values.imageFiles}
                    onChange={(files) => {
                      createFormik.setFieldValue('imageFiles', files);
                      createFormik.setFieldTouched('imageFiles', true);
                    }}
                    validationError={
                      (createFormik.touched.imageFiles ||
                        createFormik.submitCount > 0) &&
                      createFormik.errors.imageFiles
                        ? createFormik.errors.imageFiles
                        : null
                    }
                    maxCount={5}
                    maxSizeBytes={2 * 1024 * 1024}
                    accept="image/*"
                    firstImageLabel="Ảnh thu nhỏ"
                    reorderHint="Ảnh đầu tiên dùng làm ảnh thu nhỏ. Kéo handle để đổi thứ tự (tối đa 5 ảnh, mỗi ảnh ≤ 2MB)."
                    addButtonLabel="Thêm ảnh"
                  />
                </Form.Group>
              </div>
            </div>
          </Modal.Body>
          <Modal.Footer className="border-0 pt-0">
            <Button variant="light" onClick={handleCloseAddModal}>
              Huỷ bỏ
            </Button>
            <Button type="submit" variant="success" disabled={isLoading}>
              Thêm sách
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      {/* Page Header */}
      <div className="d-flex align-items-center justify-content-between mb-4">
        <div className="d-flex align-items-center gap-3">
          <div className="page-icon rounded-3 d-flex align-items-center justify-content-center text-white">
            <FaBook />
          </div>
          <h4 className="mb-0 fw-bold">Quản lý Sách</h4>
        </div>
        <Button
          className="btn-orange px-4"
          onClick={() => setShowAddModal(true)}
        >
          <FaPlus className="me-2" />
          Thêm mới
        </Button>
      </div>

      {/* Search Card */}
      <Card className="mb-4 shadow-sm border-0">
        <Card.Body className="py-3">
          <Form
            onSubmit={onSearch}
            className="d-flex flex-wrap gap-3 align-items-end"
          >
            <div style={{ maxWidth: 280, flex: 1 }}>
              <TextField
                name="name"
                maxLength={255}
                value={searchName}
                handleChange={(e) => setSearchName(e.target.value)}
                handleBlur={() => {}}
                placeholder="Tìm kiếm theo tên sách..."
              />
            </div>
            <Form.Group style={{ minWidth: 180 }}>
              <Form.Label className="small text-muted mb-1">
                Danh mục
              </Form.Label>
              <Form.Select
                value={searchCategoryId}
                onChange={(e) => setSearchCategoryId(e.target.value)}
              >
                <option value="">Tất cả</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
            <Form.Group style={{ minWidth: 180 }}>
              <Form.Label className="small text-muted mb-1">
                Nhà xuất bản
              </Form.Label>
              <Form.Select
                value={searchPublisherId}
                onChange={(e) => setSearchPublisherId(e.target.value)}
              >
                <option value="">Tất cả</option>
                {publishers.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
            <Button
              variant="outline-secondary"
              onClick={onResetSearch}
              disabled={isLoading}
            >
              Xoá lọc
            </Button>
            <Button type="submit" className="btn-search" disabled={isLoading}>
              <FaSearch className="me-2" />
              Tìm kiếm
            </Button>
          </Form>
        </Card.Body>
      </Card>

      {/* Data Table */}
      <Card className="shadow-sm border-0">
        {data.length > 0 ? (
          <>
            <Table className="mb-0">
              <thead>
                <tr>
                  <th className="stt text-center">STT</th>
                  <SortableTh
                    label="Tên sách"
                    sortKey="name"
                    sortBy={sortBy}
                    sortOrder={sortOrder}
                    onSort={handleSort}
                    align="left"
                  />
                  <th>Danh mục</th>
                  <th>Tác giả</th>
                  <th>Nhà xuất bản</th>
                  <th>Giá</th>
                  <th>Giảm giá</th>
                  <th>Số lượng</th>
                  <SortableTh
                    label="Ngày tạo"
                    sortKey="createdAt"
                    sortBy={sortBy}
                    sortOrder={sortOrder}
                    onSort={handleSort}
                  />
                  <SortableTh
                    label="Cập nhật"
                    sortKey="updatedAt"
                    sortBy={sortBy}
                    sortOrder={sortOrder}
                    onSort={handleSort}
                  />
                  <th
                    colSpan={2}
                    style={{ width: 140 }}
                    className="text-center"
                  >
                    Thao tác
                  </th>
                </tr>
              </thead>
              <tbody>
                {data.map((item, index) => (
                  <tr key={item.id}>
                    <td className="stt text-center">
                      {(pageNum - 1) * PAGINATION.PAGE_SIZE + index + 1}
                    </td>
                    <td className="fw-medium td-name">{item.name}</td>
                    <td className="text-muted small">
                      {item.categoryName ?? '-'}
                    </td>
                    <td className="text-muted small">
                      {item.authorNames ?? '-'}
                    </td>
                    <td className="text-muted small">
                      {item.publisherName ?? '-'}
                    </td>
                    <td className="text-muted small">
                      {formatUtil.formatPrice(item.price)}
                    </td>
                    <td className="text-muted small">
                      {item.discount != null ? `${item.discount}%` : '-'}
                    </td>
                    <td className="text-muted small">
                      {item.quantity != null ? item.quantity : '-'}
                    </td>
                    <td className="text-muted small">
                      {dateUtil.formatDateTime(
                        item.createdAt ?? item.created_at,
                      )}
                    </td>
                    <td className="text-muted small">
                      {dateUtil.formatDateTime(
                        item.updatedAt ?? item.updated_at,
                      )}
                    </td>
                    <TableAction
                      onEdit={onClickEditBtn}
                      onDelete={() => onClickDeleteBtn(item)}
                    />
                  </tr>
                ))}
              </tbody>
            </Table>
            <div className="p-3 border-top d-flex align-items-center justify-content-between">
              <span className="text-muted small">
                {Math.min(pageNum * PAGINATION.PAGE_SIZE, totalItems)}/
                {totalItems} kết quả
              </span>
              {totalPages > 1 ? (
                <Pagination
                  totalPage={totalPages}
                  currentPage={pageNum}
                  onChangePage={handleChangePage}
                />
              ) : (
                <div />
              )}
            </div>
          </>
        ) : !isLoading ? (
          <div className="empty-state text-center text-muted py-5">
            <FaInbox className="mb-3" />
            <p className="mb-0">Chưa có sách nào</p>
          </div>
        ) : null}
      </Card>
    </div>
  );
}

export default BookManagementPage;
