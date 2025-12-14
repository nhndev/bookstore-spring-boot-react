import { useFormik } from 'formik';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import * as Yup from 'yup';
import { Button, Col, Modal, Row, Table } from 'react-bootstrap';
import TextField from '../../../components/ui/TextField';
import validationUtil from '../../../utils/validation.util';
import { AUTHOR } from '../../../constants/label';
import authorService from '../../../services/author.service';
import { useDispatch, useSelector } from 'react-redux';
import { setLoading } from '../../../redux/slice/app.slice';
import { PAGINATION } from '../../../constants/setting';
import Pagination from '../../../components/ui/Pagination';
import messageUtil from '../../../utils/message.util';
import TableAction from '../../../components/ui/TableAction';
import { showConfirmDialog } from '../../../redux/slice/confirmDialog.slice';

function AuthorManagementPage() {
  const isLoading = useSelector((state) => state.app.isLoading);
  const [searchCondition, setSearchCondition] = useState(null);
  const [data, setData] = useState([]);
  const [totalItems, setTotalItems] = useState(0);

  const [pageNum, setPageNum] = useState(1);

  const dispatch = useDispatch();

  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);

  const totalPages = useMemo(() => {
    return Math.ceil(totalItems / PAGINATION.PAGE_SIZE);
  }, [totalItems]);

  const fetchData = useCallback(
    async (pageNum) => {
      try {
        setLoading(true);
        const { data, totalItems } = await authorService.search({
          pageNum,
          pageSize: PAGINATION.PAGE_SIZE,
          name: searchCondition?.name || null,
        });

        setData(data);
        setTotalItems(totalItems);
      } finally {
        setLoading(false);
      }
    },
    [searchCondition]
  );

  useEffect(() => {
    fetchData(pageNum);
  }, [pageNum, searchCondition, fetchData]);

  const handleChangePage = useCallback((page) => {
    setPageNum(page);
  }, []);

  const onClickEditBtn = async (id) => {
    dispatch(setLoading(true));
    try {
      const { data } = await authorService.findById(id);
      if (!data) {
        return;
      }
      editFormik.values.id = id;
      editFormik.values.name = data.name;
      editFormik.values.parent = data.parentId ?? '';
      editFormik.values.currentImage = data.imageUrl;
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
        okFunc: () => deleteRecord(item.id),
      })
    );
  };

  const deleteRecord = async (id) => {
    try {
      dispatch(setLoading(true));
      await authorService.delete(id);
      messageUtil.showSuccessMessage();
      fetchData(1);
    } finally {
      dispatch(setLoading(false));
    }
  };

  const createFormik = useFormik({
    initialValues: {
      name: '',
    },
    enableReinitialize: true,
    validateOnChange: false,
    validateOnBlur: true,
    validationSchema: Yup.object({
      name: Yup.string()
        .required(validationUtil.requiredMsg(AUTHOR.NAME))
        .max(255, validationUtil.maxLength(AUTHOR.NAME, 255)),
    }),
    onSubmit: async () => {
      const { name } = createFormik.values;
      try {
        dispatch(setLoading(true));
        await authorService.create({ name });
        messageUtil.showSuccessMessage();
        createFormik.resetForm();
        setShowAddModal(false);
        fetchData(1);
      } finally {
        dispatch(setLoading(false));
      }
    },
  });

  const editFormik = useFormik({
    initialValues: {
      id: '',
      name: '',
    },
    enableReinitialize: true,
    validateOnChange: false,
    validateOnBlur: true,
    validationSchema: Yup.object({
      name: Yup.string()
        .required(validationUtil.requiredMsg(AUTHOR.NAME))
        .max(255, validationUtil.maxLength(AUTHOR.NAME, 255)),
    }),
    onSubmit: async () => {
      const { id, name } = editFormik.values;
      try {
        dispatch(setLoading(true));
        await authorService.update(id, { name });
        messageUtil.showSuccessMessage();
        setShowEditModal(false);
        fetchData(1);
      } finally {
        dispatch(setLoading(false));
      }
    },
  });

  const onSearch = async (e) => {
    e.preventDefault();
    const { name } = searchFormik.values;
    if (!name) {
      dispatch(
        showConfirmDialog({
          message: messageUtil.getSearchConditionMissing(),
        })
      );
      return;
    }
    setSearchCondition({ name });
    setPageNum(1);
  };

  const onResetSearch = (e) => {
    e.preventDefault();
    setSearchCondition(null);
    searchFormik.resetForm();
    setPageNum(1);
  };

  const searchFormik = useFormik({
    initialValues: {
      name: '',
    },
    enableReinitialize: true,
    validateOnChange: false,
    validateOnBlur: true,
    validationSchema: Yup.object({
      name: Yup.string()
        .required(validationUtil.requiredMsg(AUTHOR.NAME))
        .max(255, validationUtil.maxLength(AUTHOR.NAME, 255)),
    }),
  });

  return (
    <Row>
      <Modal
        size="lg"
        show={showEditModal}
        onHide={() => setShowEditModal(false)}
      >
        <Modal.Header closeButton>
          <Modal.Title>Chỉnh sửa thông tin Tác giả</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <form>
            <Row>
              <Col xl={12}>
                <TextField
                  label={AUTHOR.NAME}
                  name="name"
                  maxLength={255}
                  value={editFormik.values.name}
                  touched={editFormik.touched.name}
                  handleChange={editFormik.handleChange}
                  handleBlur={editFormik.handleBlur}
                  error={editFormik.errors.name}
                />
              </Col>
            </Row>
          </form>
        </Modal.Body>
        <Modal.Footer>
          <Button
            onClick={editFormik.handleSubmit}
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
          <Modal.Title>Thêm Tác giả</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <form>
            <Row>
              <Col xl={12}>
                <TextField
                  label={AUTHOR.NAME}
                  name="name"
                  maxLength={255}
                  value={createFormik.values.name}
                  touched={createFormik.touched.name}
                  handleChange={createFormik.handleChange}
                  handleBlur={createFormik.handleBlur}
                  error={createFormik.errors.name}
                />
              </Col>
            </Row>
          </form>
        </Modal.Body>
        <Modal.Footer>
          <Button
            onClick={createFormik.handleSubmit}
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
          <div className="admin-content-header">Danh sách Tác giả</div>
          <div className="admin-content-search-condition">
            <form>
              <Row>
                <Col xl={6}>
                  <TextField
                    name="name"
                    maxLength={255}
                    value={searchFormik.values.name}
                    handleChange={searchFormik.handleChange}
                    handleBlur={searchFormik.handleBlur}
                    placeholder="Tìm kiếm tác giả"
                  />
                </Col>
              </Row>
              <Row className="action-menu">
                <Col xl={12}>
                  <Button
                    onClick={(e) => onResetSearch(e)}
                    disabled={isLoading}
                    type="submit"
                    className="mt-2"
                    variant="secondary"
                  >
                    Huỷ bộ lọc tìm kiếm
                  </Button>
                  <Button
                    onClick={(e) => onSearch(e)}
                    disabled={isLoading}
                    type="submit"
                    className="mt-2"
                  >
                    Tìm kiếm
                  </Button>
                </Col>
              </Row>
            </form>
          </div>
          <div className="admin-content-action">
            <div className="d-flex">
              <button
                type="button"
                className="btn btn-success ms-auto"
                onClick={() => setShowAddModal(true)}
              >
                Thêm Tác giả
              </button>
            </div>
          </div>
          <div className="admin-content-body">
            <Table bordered>
              <thead>
                <tr>
                  <th>STT</th>
                  <th>Tên tác giả</th>
                  <th colSpan="2">Hành động</th>
                </tr>
              </thead>
              <tbody>
                {data &&
                  data.length > 0 &&
                  data.map((item, index) => {
                    return (
                      <tr key={item.id}>
                        <td>
                          {(pageNum - 1) * PAGINATION.PAGE_SIZE + index + 1}
                        </td>
                        <td>{item.name}</td>
                        <TableAction
                          onEdit={() => onClickEditBtn(item.id)}
                          onDelete={() => onClickDeleteBtn(item)}
                        />
                      </tr>
                    );
                  })}
              </tbody>
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

export default AuthorManagementPage;
