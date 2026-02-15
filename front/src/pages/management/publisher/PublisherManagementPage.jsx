import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { useFormik } from 'formik';
import { Button, Card, Modal, Table } from 'react-bootstrap';
import { FaBuilding, FaInbox, FaPen, FaPlus, FaSearch } from 'react-icons/fa';
import { useDispatch, useSelector } from 'react-redux';
import * as Yup from 'yup';

import Pagination from '../../../components/ui/Pagination';
import TableAction from '../../../components/ui/TableAction';
import TextField from '../../../components/ui/TextField';
import { PUBLISHER } from '../../../constants/label';
import { PAGINATION } from '../../../constants/setting';
import { setLoading } from '../../../redux/slice/app.slice';
import { showConfirmDialog } from '../../../redux/slice/confirmDialog.slice';
import publisherService from '../../../services/publisher.service';
import dateUtil from '../../../utils/date.util';
import messageUtil from '../../../utils/message.util';
import validationUtil from '../../../utils/validation.util';
import '../management-common.scss';

function PublisherManagementPage() {
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
    async (page) => {
      try {
        dispatch(setLoading(true));
        const res = await publisherService.search({
          pageNum: page,
          pageSize: PAGINATION.PAGE_SIZE,
          name: searchCondition?.name || null,
        });
        setData(res.data ?? []);
        setTotalItems(res.totalItems ?? 0);
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

  const onClickEditBtn = (item) => {
    editFormik.setValues({
      id: item.id,
      name: item.name,
    });
    setShowEditModal(true);
  };

  const onClickDeleteBtn = (item) => {
    dispatch(
      showConfirmDialog({
        message: messageUtil.getDeleteConfirmationMsg(
          `xoá <strong>Nhà xuất bản ${item.name}</strong>`,
        ),
        okFunc: () => deleteRecord(item.id),
      }),
    );
  };

  const deleteRecord = async (id) => {
    try {
      dispatch(setLoading(true));
      await publisherService.delete(id);
      messageUtil.showSuccessMessage();
      fetchData(1);
    } catch {
      // Error page or toast shown by httpClient
    } finally {
      dispatch(setLoading(false));
    }
  };

  const createFormik = useFormik({
    initialValues: { name: '' },
    enableReinitialize: true,
    validateOnChange: false,
    validateOnBlur: true,
    validationSchema: Yup.object({
      name: Yup.string()
        .required(validationUtil.requiredMsg(PUBLISHER.NAME))
        .max(255, validationUtil.maxLength(PUBLISHER.NAME, 255)),
    }),
    onSubmit: async () => {
      const { name } = createFormik.values;
      try {
        dispatch(setLoading(true));
        await publisherService.create({ name });
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

  const editFormik = useFormik({
    initialValues: { id: '', name: '' },
    enableReinitialize: true,
    validateOnChange: false,
    validateOnBlur: true,
    validationSchema: Yup.object({
      name: Yup.string()
        .required(validationUtil.requiredMsg(PUBLISHER.NAME))
        .max(255, validationUtil.maxLength(PUBLISHER.NAME, 255)),
    }),
    onSubmit: async () => {
      const { id, name } = editFormik.values;
      try {
        dispatch(setLoading(true));
        await publisherService.update(id, { name });
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
        .required(validationUtil.requiredMsg(PUBLISHER.NAME))
        .max(255, validationUtil.maxLength(PUBLISHER.NAME, 255)),
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

  const handleCloseAddModal = () => {
    createFormik.resetForm();
    setShowAddModal(false);
  };

  const handleCloseEditModal = () => {
    editFormik.resetForm();
    setShowEditModal(false);
  };

  return (
    <div className="management-page publisher-management">
      {/* Edit Modal */}
      <Modal centered show={showEditModal} onHide={handleCloseEditModal}>
        <Modal.Header closeButton className="border-0 pb-0">
          <Modal.Title className="d-flex align-items-center gap-2 fs-5">
            <span className="modal-icon bg-primary rounded-2 d-flex align-items-center justify-content-center text-white">
              <FaPen />
            </span>
            Chỉnh sửa Nhà xuất bản
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="pt-3">
          <TextField
            label={PUBLISHER.NAME}
            name="name"
            maxLength={255}
            value={editFormik.values.name}
            touched={editFormik.touched.name}
            handleChange={editFormik.handleChange}
            handleBlur={editFormik.handleBlur}
            error={editFormik.errors.name}
          />
        </Modal.Body>
        <Modal.Footer className="border-0 pt-0">
          <Button variant="light" onClick={handleCloseEditModal}>
            Huỷ bỏ
          </Button>
          <Button
            variant="primary"
            onClick={editFormik.handleSubmit}
            disabled={isLoading}
          >
            Lưu thay đổi
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Add Modal */}
      <Modal centered show={showAddModal} onHide={handleCloseAddModal}>
        <Modal.Header closeButton className="border-0 pb-0">
          <Modal.Title className="d-flex align-items-center gap-2 fs-5">
            <span className="modal-icon bg-success rounded-2 d-flex align-items-center justify-content-center text-white">
              <FaPlus />
            </span>
            Thêm Nhà xuất bản mới
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="pt-3">
          <TextField
            label={PUBLISHER.NAME}
            name="name"
            maxLength={255}
            value={createFormik.values.name}
            touched={createFormik.touched.name}
            handleChange={createFormik.handleChange}
            handleBlur={createFormik.handleBlur}
            error={createFormik.errors.name}
          />
        </Modal.Body>
        <Modal.Footer className="border-0 pt-0">
          <Button variant="light" onClick={handleCloseAddModal}>
            Huỷ bỏ
          </Button>
          <Button
            variant="success"
            onClick={createFormik.handleSubmit}
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
            <FaBuilding />
          </div>
          <h4 className="mb-0 fw-bold">Quản lý Nhà xuất bản</h4>
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
          <div className="d-flex gap-3 align-items-center">
            <div style={{ maxWidth: 350, flex: 1 }}>
              <TextField
                name="name"
                maxLength={255}
                value={searchFormik.values.name}
                handleChange={searchFormik.handleChange}
                handleBlur={searchFormik.handleBlur}
                placeholder="Tìm kiếm nhà xuất bản..."
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
        {data.length > 0 ? (
          <>
            <Table className="mb-0">
              <thead>
                <tr>
                  <th className="stt text-center">STT</th>
                  <th>Tên nhà xuất bản</th>
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
              <tbody>
                {data.map((item, index) => (
                  <tr key={item.id}>
                    <td className="stt text-center">
                      {(pageNum - 1) * PAGINATION.PAGE_SIZE + index + 1}
                    </td>
                    <td className="fw-medium">{item.name}</td>
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
                      onEdit={() => onClickEditBtn(item)}
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
            <p className="mb-0">Chưa có nhà xuất bản nào</p>
          </div>
        ) : null}
      </Card>
    </div>
  );
}

export default PublisherManagementPage;
