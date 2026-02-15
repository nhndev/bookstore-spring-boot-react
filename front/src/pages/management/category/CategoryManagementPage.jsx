import React, {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';

import { useFormik } from 'formik';
import { Button, Card, Col, Modal, Row, Table } from 'react-bootstrap';
import { FaInbox, FaPen, FaPlus, FaSearch, FaTags } from 'react-icons/fa';
import { useDispatch, useSelector } from 'react-redux';
import * as Yup from 'yup';

import FileUpload from '../../../components/ui/FileUpload';
import Pagination from '../../../components/ui/Pagination';
import Select from '../../../components/ui/Select';
import TableAction from '../../../components/ui/TableAction';
import TextField from '../../../components/ui/TextField';
import { CATEGORY } from '../../../constants/label';
import { PAGINATION } from '../../../constants/setting';
import { setLoading } from '../../../redux/slice/app.slice';
import { showConfirmDialog } from '../../../redux/slice/confirmDialog.slice';
import categoryService from '../../../services/category.service';
import dateUtil from '../../../utils/date.util';
import messageUtil from '../../../utils/message.util';
import validationUtil from '../../../utils/validation.util';
import '../management-common.scss';

function CategoryManagementPage() {
  const isLoading = useSelector((state) => state.app.isLoading);
  const [searchCondition, setSearchCondition] = useState(null);
  const [categoryData, setCategoryData] = useState([]);
  const [totalCategories, setTotalCategories] = useState(0);
  const [categoryOptions, setCategoryOptions] = useState([]);
  const fileUploadRef = useRef(null);
  const [pageNum, setPageNum] = useState(1);
  const dispatch = useDispatch();
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);

  const totalPages = useMemo(() => {
    return Math.ceil(totalCategories / PAGINATION.PAGE_SIZE);
  }, [totalCategories]);

  const fetchData = useCallback(
    async (pageNum) => {
      try {
        dispatch(setLoading(true));
        const { data, totalItems } = await categoryService.search({
          pageNum,
          pageSize: PAGINATION.PAGE_SIZE,
          name: searchCondition?.name || '',
        });

        const categoryMap = new Map();
        data.forEach((item) => {
          categoryMap.set(item.id, item.name);
        });

        data.forEach((item) => {
          const parentId = item.parentId;
          if (parentId) {
            item.parentName = categoryMap.get(parentId);
          }
        });

        const groupedCategories = data.reduce((acc, item) => {
          const parentId = item.parentId || '0';
          if (!acc[parentId]) {
            acc[parentId] = [];
          }
          acc[parentId].push(item);
          return acc;
        }, {});

        setCategoryData(groupedCategories);
        setTotalCategories(totalItems);
      } catch {
        // Error page shown by httpClient interceptor for 401/403/500
      } finally {
        dispatch(setLoading(false));
      }
    },
    [searchCondition, dispatch],
  );

  useEffect(() => {
    fetchData(pageNum);
  }, [pageNum, searchCondition, fetchData]);

  const handleChangePage = useCallback((page) => {
    setPageNum(page);
  }, []);

  const renderCategoryOptions = async () => {
    try {
      const { data } = await categoryService.findAllBasicInfo();
      data.unshift({ id: '', name: 'Không có' });
      setCategoryOptions(
        data.map((item) => ({ key: item.id, value: item.name })),
      );
    } catch {
      // Error page or toast shown by httpClient
    }
  };

  const onChangeImage = (e, formik) => {
    const file = e?.target?.files[0];
    formik?.setFieldValue('image', file);
    formik?.setFieldTouched('image', true);
    formik?.validateField('image');
  };

  const onClickEditBtn = async (id) => {
    dispatch(setLoading(true));
    try {
      const { data } = await categoryService.findById(id);
      if (!data) {
        return;
      }
      editCategoryFormik.setValues({
        id,
        name: data.name,
        parent: data.parentId ?? '',
        currentImage: data.imageUrl,
        image: null,
      });
      await renderCategoryOptions();
      setShowEditModal(true);
    } catch {
      // Error page or toast shown by httpClient
    } finally {
      dispatch(setLoading(false));
    }
  };

  const onClickDeleteBtn = (item) => {
    dispatch(
      showConfirmDialog({
        message: messageUtil.getDeleteConfirmationMsg(
          `xoá <strong>Danh mục ${item.name}</strong>`,
        ),
        okFunc: () => deleteCategory(item.id),
      }),
    );
  };

  const deleteCategory = async (id) => {
    try {
      dispatch(setLoading(true));
      await categoryService.delete(id);
      messageUtil.showSuccessMessage();
      fetchData(1);
    } catch {
      // Error page or toast shown by httpClient
    } finally {
      dispatch(setLoading(false));
    }
  };

  const imageValidation = Yup.mixed()
    .nullable()
    .notRequired()
    .test(
      'FILE_SIZE',
      validationUtil.sizeFile(),
      (value) => !value || (value && value.size < 2 * 1024 * 1024),
    )
    .test(
      'FILE_FORMAT',
      validationUtil.formatFile(),
      (value) =>
        !value ||
        (value &&
          ['image/png', 'image/gif', 'image/jpeg'].includes(value?.type)),
    );

  const createCategoryFormik = useFormik({
    initialValues: {
      name: '',
      parent: categoryOptions[0]?.key ?? '',
      image: null,
    },
    enableReinitialize: true,
    validateOnChange: false,
    validateOnBlur: true,
    validationSchema: Yup.object({
      name: Yup.string()
        .required(validationUtil.requiredMsg(CATEGORY.NAME))
        .max(255, validationUtil.maxLength(CATEGORY.NAME, 255)),
      image: imageValidation,
    }),
    onSubmit: async () => {
      const { name, parent, image } = createCategoryFormik.values;
      const request = new FormData();
      request.append(
        'data',
        new Blob([JSON.stringify({ name, parentId: parent ? parent : null })], {
          type: 'application/json',
        }),
      );
      request.append('file', image);
      try {
        dispatch(setLoading(true));
        await categoryService.create(request, {
          headers: { 'Content-Type': undefined },
        });
        messageUtil.showSuccessMessage();
        createCategoryFormik.resetForm();
        setShowAddModal(false);
        fetchData(1);
      } catch {
        // Error page or toast shown by httpClient
      } finally {
        dispatch(setLoading(false));
      }
    },
  });

  const editCategoryFormik = useFormik({
    initialValues: {
      id: '',
      name: '',
      parent: '',
      currentImage: '',
      image: null,
    },
    enableReinitialize: true,
    validateOnChange: false,
    validateOnBlur: true,
    validationSchema: Yup.object({
      name: Yup.string()
        .required(validationUtil.requiredMsg(CATEGORY.NAME))
        .max(255, validationUtil.maxLength(CATEGORY.NAME, 255)),
      image: imageValidation,
    }),
    onSubmit: async () => {
      const { id, name, parent, image } = editCategoryFormik.values;
      const request = new FormData();
      request.append(
        'data',
        new Blob([JSON.stringify({ name, parentId: parent ? parent : null })], {
          type: 'application/json',
        }),
      );
      request.append('file', image);
      try {
        dispatch(setLoading(true));
        await categoryService.update(id, request, {
          headers: { 'Content-Type': undefined },
        });
        messageUtil.showSuccessMessage();
        setShowEditModal(false);
        fetchData(1);
      } catch {
        // Error page or toast shown by httpClient
      } finally {
        dispatch(setLoading(false));
      }
    },
  });

  const searchFormik = useFormik({
    initialValues: { name: '' },
    enableReinitialize: true,
    validateOnChange: false,
    validateOnBlur: true,
    validationSchema: Yup.object({
      name: Yup.string()
        .required(validationUtil.requiredMsg(CATEGORY.NAME))
        .max(255, validationUtil.maxLength(CATEGORY.NAME, 255)),
    }),
  });

  const onSearch = (e) => {
    e.preventDefault();
    const { name } = searchFormik.values;
    setSearchCondition(name ? { name } : null);
    setPageNum(1);
  };

  const onResetSearch = (e) => {
    e.preventDefault();
    setSearchCondition(null);
    searchFormik.resetForm();
    setPageNum(1);
  };

  const onClickShowAddCategoryModal = async () => {
    setShowAddModal(true);
    try {
      dispatch(setLoading(true));
      await renderCategoryOptions();
    } finally {
      dispatch(setLoading(false));
    }
  };

  const handleCloseAddModal = () => {
    createCategoryFormik.resetForm();
    setShowAddModal(false);
  };

  const handleCloseEditModal = () => {
    editCategoryFormik.resetForm();
    setShowEditModal(false);
  };

  const renderRows = () => {
    const rows = [];
    let globalIndex = 1;
    Object.keys(categoryData).forEach((parentId) => {
      const group = categoryData[parentId];
      group.forEach((item, index) => {
        rows.push(
          <tr key={item.id}>
            <td className="stt text-center">
              {(pageNum - 1) * PAGINATION.PAGE_SIZE + globalIndex++}
            </td>
            <td className="fw-medium">{item.name}</td>
            {index === 0 && (
              <td rowSpan={group.length}>
                {parentId === '0' ? '' : group[0].parentName}
              </td>
            )}
            <td className="text-muted small">
              {dateUtil.formatDateTime(item.createdAt ?? item.created_at)}
            </td>
            <td className="text-muted small">
              {dateUtil.formatDateTime(item.updatedAt ?? item.updated_at)}
            </td>
            <TableAction
              onEdit={() => onClickEditBtn(item.id)}
              onDelete={() => onClickDeleteBtn(item)}
            />
          </tr>,
        );
      });
    });
    return rows;
  };

  const hasData = Object.keys(categoryData).length > 0;

  return (
    <div className="management-page category-management">
      {/* Edit Modal */}
      <Modal
        centered
        size="lg"
        show={showEditModal}
        onHide={handleCloseEditModal}
      >
        <Modal.Header closeButton className="border-0 pb-0">
          <Modal.Title className="d-flex align-items-center gap-2 fs-5">
            <span className="modal-icon bg-primary rounded-2 d-flex align-items-center justify-content-center text-white">
              <FaPen />
            </span>
            Chỉnh sửa Danh mục sách
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="pt-3">
          <Row>
            <Col xl={12}>
              <TextField
                label={CATEGORY.NAME}
                name="name"
                maxLength={255}
                value={editCategoryFormik.values.name}
                touched={editCategoryFormik.touched.name}
                handleChange={editCategoryFormik.handleChange}
                handleBlur={editCategoryFormik.handleBlur}
                error={editCategoryFormik.errors.name}
              />
            </Col>
            <Col xl={12}>
              <Select
                label={CATEGORY.PARENT}
                name="parent"
                options={categoryOptions}
                value={editCategoryFormik.values.parent}
                touched={editCategoryFormik.touched.parent}
                handleChange={editCategoryFormik.handleChange}
                handleBlur={editCategoryFormik.handleBlur}
                error={editCategoryFormik.errors.parent}
              />
            </Col>
            {!editCategoryFormik.values.parent && (
              <Col xl={12} className="mt-2">
                <FileUpload
                  ref={fileUploadRef}
                  label={CATEGORY.IMAGE}
                  name="image"
                  file={editCategoryFormik.values.image}
                  accept="image/png, image/gif, image/jpeg"
                  handleChange={(e) => onChangeImage(e, editCategoryFormik)}
                  handleBlur={(e) => onChangeImage(e, editCategoryFormik)}
                  handleClickImage={() => fileUploadRef.current.click()}
                  hidden={!!editCategoryFormik?.values?.currentImage}
                  currentImage={editCategoryFormik.values.currentImage}
                  error={
                    editCategoryFormik.touched.image &&
                    editCategoryFormik.errors.image
                  }
                />
              </Col>
            )}
          </Row>
        </Modal.Body>
        <Modal.Footer className="border-0 pt-0">
          <Button variant="light" onClick={handleCloseEditModal}>
            Huỷ bỏ
          </Button>
          <Button
            variant="primary"
            onClick={editCategoryFormik.handleSubmit}
            disabled={isLoading}
          >
            Lưu thay đổi
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Add Modal */}
      <Modal
        centered
        size="lg"
        show={showAddModal}
        onHide={handleCloseAddModal}
      >
        <Modal.Header closeButton className="border-0 pb-0">
          <Modal.Title className="d-flex align-items-center gap-2 fs-5">
            <span className="modal-icon bg-success rounded-2 d-flex align-items-center justify-content-center text-white">
              <FaPlus />
            </span>
            Thêm Danh mục sách mới
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="pt-3">
          <Row>
            <Col xl={12}>
              <TextField
                label={CATEGORY.NAME}
                name="name"
                maxLength={255}
                value={createCategoryFormik.values.name}
                touched={createCategoryFormik.touched.name}
                handleChange={createCategoryFormik.handleChange}
                handleBlur={createCategoryFormik.handleBlur}
                error={createCategoryFormik.errors.name}
              />
            </Col>
            <Col xl={12}>
              <Select
                label={CATEGORY.PARENT}
                name="parent"
                options={categoryOptions}
                value={createCategoryFormik.values.parent}
                touched={createCategoryFormik.touched.parent}
                handleChange={createCategoryFormik.handleChange}
                handleBlur={createCategoryFormik.handleBlur}
                error={createCategoryFormik.errors.parent}
              />
            </Col>
            {!createCategoryFormik.values.parent && (
              <Col xl={12} className="mt-2">
                <FileUpload
                  ref={fileUploadRef}
                  label={CATEGORY.IMAGE}
                  name="image"
                  file={createCategoryFormik.values.image}
                  accept="image/png, image/gif, image/jpeg"
                  handleChange={(e) => onChangeImage(e, createCategoryFormik)}
                  handleBlur={(e) => onChangeImage(e, createCategoryFormik)}
                  handleClickImage={() => fileUploadRef.current.click()}
                  error={
                    createCategoryFormik.touched.image &&
                    createCategoryFormik.errors.image
                  }
                />
              </Col>
            )}
          </Row>
        </Modal.Body>
        <Modal.Footer className="border-0 pt-0">
          <Button variant="light" onClick={handleCloseAddModal}>
            Huỷ bỏ
          </Button>
          <Button
            variant="success"
            onClick={createCategoryFormik.handleSubmit}
            disabled={isLoading}
          >
            Thêm mới
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Page Header */}
      <div className="d-flex align-items-center justify-content-between mb-4">
        <div className="d-flex align-items-center gap-3">
          <div className="page-icon rounded-3 d-flex align-items-center justify-content-center text-white">
            <FaTags />
          </div>
          <h4 className="mb-0 fw-bold">Quản lý Danh mục sách</h4>
        </div>
        <Button
          className="btn-orange px-4"
          onClick={onClickShowAddCategoryModal}
        >
          <FaPlus className="me-2" />
          Thêm mới
        </Button>
      </div>

      {/* Search Card */}
      <Card className="mb-4 shadow-sm border-0">
        <Card.Body className="py-3">
          <div className="d-flex gap-3 align-items-center">
            <div style={{ maxWidth: 350, flex: 1 }}>
              <TextField
                name="name"
                maxLength={255}
                value={searchFormik.values.name}
                handleChange={searchFormik.handleChange}
                handleBlur={searchFormik.handleBlur}
                placeholder="Tìm kiếm danh mục..."
              />
            </div>
            <Button
              variant="outline-secondary"
              onClick={onResetSearch}
              disabled={isLoading}
            >
              Xoá lọc
            </Button>
            <Button
              className="btn-search"
              onClick={onSearch}
              disabled={isLoading}
            >
              <FaSearch className="me-2" />
              Tìm kiếm
            </Button>
          </div>
        </Card.Body>
      </Card>

      {/* Data Table */}
      <Card className="shadow-sm border-0">
        {hasData ? (
          <>
            <Table className="mb-0">
              <thead>
                <tr>
                  <th className="stt text-center">STT</th>
                  <th>Tên danh mục</th>
                  <th>Danh mục cha</th>
                  <th>Ngày tạo</th>
                  <th>Cập nhật</th>
                  <th
                    colSpan={2}
                    style={{ width: 140 }}
                    className="text-center"
                  >
                    Thao tác
                  </th>
                </tr>
              </thead>
              <tbody>{renderRows()}</tbody>
            </Table>
            <div className="p-3 border-top d-flex align-items-center justify-content-between">
              <span className="text-muted small">
                {Math.min(pageNum * PAGINATION.PAGE_SIZE, totalCategories)}/
                {totalCategories} kết quả
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
            <p className="mb-0">Chưa có danh mục nào</p>
          </div>
        ) : null}
      </Card>
    </div>
  );
}

export default CategoryManagementPage;
