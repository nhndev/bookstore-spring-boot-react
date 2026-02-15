import React from 'react';
import { FaSort, FaSortDown, FaSortUp } from 'react-icons/fa';
import PropTypes from 'prop-types';

function SortableTh({ label, sortKey, sortBy, sortOrder, onSort }) {
  const handleKeyDown = (e) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      onSort(sortKey);
    }
  };

  const renderSortIcon = () => {
    if (sortBy !== sortKey) return <FaSort className="sort-icon" />;
    return sortOrder === 'asc' ? (
      <FaSortUp className="sort-icon" />
    ) : (
      <FaSortDown className="sort-icon" />
    );
  };

  return (
    <th
      className="sortable-th"
      onClick={() => onSort(sortKey)}
      onKeyDown={handleKeyDown}
      role="button"
      tabIndex={0}
    >
      {label}
      {renderSortIcon()}
    </th>
  );
}

SortableTh.propTypes = {
  label: PropTypes.string.isRequired,
  sortKey: PropTypes.string.isRequired,
  sortBy: PropTypes.string,
  sortOrder: PropTypes.oneOf(['asc', 'desc']),
  onSort: PropTypes.func.isRequired,
};

SortableTh.defaultProps = {
  sortBy: null,
  sortOrder: 'asc',
};

export default SortableTh;
