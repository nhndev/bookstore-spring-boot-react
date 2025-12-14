import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Button, Modal } from 'react-bootstrap';
import { setShow } from '../../../redux/slice/confirmDialog.slice';

function ConfirmDialog() {
  const dispatch = useDispatch();
  const { isShow, message, okFunc, cancelFunc } = useSelector(
    (state) => state.confirmDialog
  );

  const onClickCancel = () => {
    if (cancelFunc) {
      cancelFunc();
    }
    dispatch(setShow(false))
  };

  const onClickOk = async () => {
    if (okFunc) {
      await okFunc();
    }
    dispatch(setShow(false))
  };

  return (
    <Modal size='lg' show={isShow} onHide={onClickCancel}>
      <Modal.Header closeButton>
        <Modal.Title>Xác nhận</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <div dangerouslySetInnerHTML={{__html: message}}></div>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onClickCancel}>
          Huỷ thao tác
        </Button>
        <Button variant="primary" onClick={onClickOk}>
          Tiếp tục
        </Button>
      </Modal.Footer>
    </Modal>
  );
}

export default ConfirmDialog;
