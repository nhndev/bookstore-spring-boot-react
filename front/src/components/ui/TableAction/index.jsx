import React from 'react';
import PropTypes from 'prop-types';
import './TableAction.scss';
import { FaPen, FaTimes } from 'react-icons/fa';
import { OverlayTrigger, Tooltip } from 'react-bootstrap';

TableAction.propTypes = {
  rowId: PropTypes.string,
  onEdit: PropTypes.func,
  onDelete: PropTypes.func,
};

function TableAction({ onEdit, onDelete }) {
  return (
    <>
      <td className="table-action edit">
        <div className="wrapper">
          <OverlayTrigger
            placement="top"
            overlay={<Tooltip id="edit-tooltip">Thay đổi thông tin</Tooltip>}
          >
            <span>
              <FaPen onClick={onEdit} />
            </span>
          </OverlayTrigger>
        </div>
      </td>
      <td className="table-action delete">
        <div className="wrapper">
          <OverlayTrigger
            placement="top"
            overlay={<Tooltip id="edit-tooltip">Xoá</Tooltip>}
          >
            <span>
              <FaTimes onClick={onDelete} />
            </span>
          </OverlayTrigger>
        </div>
      </td>
    </>
  );
}

export default TableAction;
