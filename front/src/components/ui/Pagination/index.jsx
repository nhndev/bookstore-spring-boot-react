import React, { memo } from 'react';
import PropTypes from 'prop-types';
import { Pagination as BsPagination } from 'react-bootstrap';

Pagination.propTypes = {
  totalPage: PropTypes.number,
  currentPage: PropTypes.number,
  onChangePage: PropTypes.func
};


function Pagination({totalPage, currentPage, onChangePage}) {
    let items = []
    if (currentPage > 1) {
        items.push(<BsPagination.Prev key="prev" onClick={() => onChangePage(currentPage -1)} />)
    }

    for (let page = 1; page <= totalPage; page++) {
        items.push(
            <BsPagination.Item onClick={() => onChangePage(page)} key={page} active={page === currentPage}>
                {page}
            </BsPagination.Item>,
        )
    }

    if (currentPage < totalPage) {
        items.push(<BsPagination.Next key="next" onClick={() => onChangePage(currentPage + 1)} />)
    }

    return (
        <BsPagination>{items}</BsPagination>
    )
}

export default memo(Pagination)
