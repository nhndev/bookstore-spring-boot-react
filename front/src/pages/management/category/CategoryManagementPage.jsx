import { useFormik } from 'formik';
import React, {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import * as Yup from 'yup';
import { Button, Col, Modal, Row, Table } from 'react-bootstrap';
import TextField from '../../../components/ui/TextField';
import validationUtil from '../../../utils/validation.util';
import { CATEGORY } from '../../../constants/label';
import FileUpload from '../../../components/ui/FileUpload';
import categoryService from '../../../services/category.service';
import { useDispatch, useSelector } from 'react-redux';
import { setLoading } from '../../../redux/slice/app.slice';
import Select from '../../../components/ui/Select';
import { PAGINATION } from '../../../constants/setting';
import Pagination from '../../../components/ui/Pagination';
import messageUtil from '../../../utils/message.util';
import TableAction from '../../../components/ui/TableAction';
import domUtil from '../../../utils/dom.util';
import { showConfirmDialog } from '../../../redux/slice/confirmDialog.slice';

function CategoryManagementPage() {
  const isLoading = useSelector((state) => state.app.isLoading);
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

  useEffect(() => {
    fetchData(pageNum);
  }, [pageNum]);

  const fetchData = async (pageNum) => {
    try {
      setLoading(true);
      const { data, totalItems } = await categoryService.search({
        pageNum,
        pageSize: PAGINATION.PAGE_SIZE,
        name: '',
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
    } finally {
      setLoading(false);
    }
  };

  const renderRows = () => {
    const rows = [];
    let globalIndex = 1;
    Object.keys(categoryData).forEach((parentId) => {
      const group = categoryData[parentId];
      group.forEach((item, index) => {
        const id = domUtil.buildRowId('category-management', index);
        rows.push(
          <tr id={id} key={item.id} className={parentId}>
            <td>{globalIndex++}</td>
            <td>{item.name}</td>
            {index === 0 && (
              <td rowSpan={group.length}>
                {parentId === '0' ? '' : group[0].parentName}
              </td>
            )}
            <TableAction
              rowId={id}
              onEdit={() => onClickEditBtn(item.id)}
              onDelete={() => onClickDeleteBtn(item)}
            />
          </tr>
        );
      });
    });
    return rows;
  };

  const handleChangePage = useCallback((page) => {
    setPageNum(page);
  }, []);

  const renderCategoryOptions = async () => {
    const { data } = await categoryService.findAllBasicInfo();
    data.unshift({
      id: '',
      name: 'Không có',
    });
    setCategoryOptions(
      data.map((item) => ({ key: item.id, value: item.name }))
    );
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
      editCategoryFormik.values.id = id;
      editCategoryFormik.values.name = data.name;
      editCategoryFormik.values.parent = data.parentId ?? '';
      editCategoryFormik.values.currentImage = data.imageUrl;
      await renderCategoryOptions();
      setShowEditModal(true);
    } finally {
      dispatch(setLoading(false));
    }
  };

  const onClickDeleteBtn = (item) => {
    dispatch(
      showConfirmDialog({
        message: messageUtil.getDeleteConfirmationMsg(
          `xoá <strong>Danh mục ${item.name}</strong>`
        ),
        okFunc: () => deleteCategory(item.id),
      })
    );
  };

  const deleteCategory = async (id) => {
    try {
      dispatch(setLoading(true));
      await categoryService.delete(id);
      messageUtil.showSuccessMessage();
      fetchData(pageNum);
    } finally {
      dispatch(setLoading(false));
    }
  };

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
      image: Yup.mixed()
        .nullable()
        .notRequired()
        .test(
          'FILE_SIZE',
          validationUtil.sizeFile(),
          (value) => !value || (value && value.size < 2 * 1024 * 1024)
        )
        .test(
          'FILE_FORMAT',
          validationUtil.formatFile(),
          (value) =>
            !value ||
            (value &&
              ['image/png', 'image/gif', 'image/jpeg'].includes(value?.type))
        ),
    }),
    onSubmit: async () => {
      const { name, parent, image } = createCategoryFormik.values;
      const request = new FormData();
      request.append(
        'data',
        new Blob([JSON.stringify({ name, parentId: parent ? parent : null })], {
          type: 'application/json',
        })
      );
      request.append('file', image);
      try {
        dispatch(setLoading(true));
        await categoryService.create(request, {
          headers: {
            'Content-Type': undefined,
          },
        });
        messageUtil.showSuccessMessage();
        createCategoryFormik.resetForm();
        createCategoryFormik.setFieldValue('image', null);
        setShowAddModal(false);
        fetchData(pageNum);
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
      image: Yup.mixed()
        .nullable()
        .notRequired()
        .test(
          'FILE_SIZE',
          validationUtil.sizeFile(),
          (value) => !value || (value && value.size < 2 * 1024 * 1024)
        )
        .test(
          'FILE_FORMAT',
          validationUtil.formatFile(),
          (value) =>
            !value ||
            (value &&
              ['image/png', 'image/gif', 'image/jpeg'].includes(value?.type))
        ),
    }),
    onSubmit: async () => {
      const { id, name, parent, image } = editCategoryFormik.values;
      const request = new FormData();
      request.append(
        'data',
        new Blob([JSON.stringify({ name, parentId: parent ? parent : null })], {
          type: 'application/json',
        })
      );
      request.append('file', image);
      try {
        dispatch(setLoading(true));
        await categoryService.update(id, request, {
          headers: {
            'Content-Type': undefined,
          },
        });
        messageUtil.showSuccessMessage();
        editCategoryFormik.setFieldValue('image', null);
        setShowEditModal(false);
        fetchData(pageNum);
      } finally {
        dispatch(setLoading(false));
      }
    },
  });

  return (
    <Row>
      <Modal
        size="lg"
        show={showEditModal}
        onHide={() => setShowEditModal(false)}
      >
        <Modal.Header closeButton>
          <Modal.Title>Chỉnh sửa Danh mục sách</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <form>
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
          </form>
        </Modal.Body>
        <Modal.Footer>
          <Button
            onClick={editCategoryFormik.handleSubmit}
            disabled={isLoading}
            type="submit"
            variant="success"
            className="mt-2"
          >
            Lưu
          </Button>
        </Modal.Footer>
      </Modal>
      <Modal
        size="lg"
        show={showAddModal}
        onHide={() => setShowAddModal(false)}
      >
        <Modal.Header closeButton>
          <Modal.Title>Thêm Danh mục sách</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <form>
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
          </form>
        </Modal.Body>
        <Modal.Footer>
          <Button
            onClick={createCategoryFormik.handleSubmit}
            disabled={isLoading}
            type="submit"
            variant="success"
            className="mt-2"
          >
            Lưu
          </Button>
        </Modal.Footer>
      </Modal>
      <Col xl={12}>
        <div className="admin-content-wrapper">
          <div className="admin-content-header">Danh mục sách</div>
          <div className="admin-content-action">
            <div className="d-flex">
              <button
                type="button"
                className="btn btn-success ms-auto"
                onClick={onClickShowAddCategoryModal}
              >
                Thêm Danh mục
              </button>
            </div>
          </div>
          <div className="admin-content-body">
            <Table bordered>
              <thead>
                <tr>
                  <th>STT</th>
                  <th>Tên danh mục</th>
                  <th>Danh mục cha</th>
                  <th colSpan="2">Hành động</th>
                </tr>
              </thead>
              <tbody>{renderRows()}</tbody>
            </Table>
            <div className="admin-content-pagination">
              <Row>
                <Col xl={12}>
                  {totalPages > 1 ? (
                    <Pagination
                      totalPage={totalPages}
                      currentPage={pageNum}
                      onChangePage={handleChangePage}
                    />
                  ) : null}
                </Col>
              </Row>
            </div>
          </div>
        </div>
      </Col>
    </Row>
  );
}

export default CategoryManagementPage;
