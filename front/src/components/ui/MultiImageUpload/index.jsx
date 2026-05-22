import React, {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';

import {
  DndContext,
  KeyboardSensor,
  PointerSensor,
  closestCenter,
  useSensor,
  useSensors,
} from '@dnd-kit/core';
import {
  SortableContext,
  arrayMove,
  sortableKeyboardCoordinates,
  useSortable,
  verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import PropTypes from 'prop-types';
import { Form } from 'react-bootstrap';
import { FaPlus } from 'react-icons/fa';

function SortableImageItem({
  file,
  previewUrl,
  index,
  total,
  firstImageLabel,
  onRemove,
}) {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: String(index) });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  return (
    <div ref={setNodeRef} style={style} className="multi-image-upload__item">
      <div className="multi-image-upload__preview-wrap">
        <img
          src={previewUrl}
          alt={file.name}
          className="multi-image-upload__preview-img"
        />
        {index === 0 && firstImageLabel && (
          <span className="multi-image-upload__thumb-badge">
            {firstImageLabel}
          </span>
        )}
        <div
          className="multi-image-upload__drag-handle"
          {...attributes}
          {...listeners}
          title="Kéo để đổi thứ tự"
        >
          ⋮⋮
        </div>
        <button
          type="button"
          className="multi-image-upload__remove"
          onClick={() => onRemove(index)}
          title="Xoá ảnh"
          aria-label="Remove image"
        >
          ×
        </button>
      </div>
      <span className="multi-image-upload__order">
        {index + 1}/{total}
      </span>
    </div>
  );
}

SortableImageItem.propTypes = {
  file: PropTypes.instanceOf(File).isRequired,
  previewUrl: PropTypes.string.isRequired,
  index: PropTypes.number.isRequired,
  total: PropTypes.number.isRequired,
  firstImageLabel: PropTypes.string,
  onRemove: PropTypes.func.isRequired,
};

function MultiImageUpload({
  value = [],
  onChange,
  maxCount,
  maxSizeBytes,
  accept = 'image/*',
  label,
  firstImageLabel = 'Ảnh thu nhỏ',
  hint,
  reorderHint,
  addButtonLabel = 'Thêm ảnh',
  validationError,
  onAddButtonClick,
  className,
  inputRef: inputRefProp,
}) {
  const fileInputRef = useRef(null);
  const resolvedInputRef = inputRefProp || fileInputRef;
  const files = useMemo(() => (Array.isArray(value) ? value : []), [value]);
  const [previewUrls, setPreviewUrls] = useState([]);
  const [internalError, setInternalError] = useState(null);
  const displayError = internalError || validationError;

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: { distance: 8 },
    }),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    }),
  );

  useEffect(() => {
    if (files.length === 0) {
      setPreviewUrls([]);
      return () => {};
    }
    const urls = files.map((f) => URL.createObjectURL(f));
    setPreviewUrls(urls);
    return () => urls.forEach((u) => URL.revokeObjectURL(u));
  }, [files]);

  const handleFileChange = useCallback(
    (e) => {
      setInternalError(null);
      const inputFiles = e.target.files;
      if (!inputFiles?.length) return;
      const newFiles = Array.from(inputFiles);
      const limit = typeof maxCount === 'number' ? maxCount : 0;
      const remaining = limit - files.length;
      if (remaining <= 0) {
        setInternalError(`Tối đa ${limit} ảnh.`);
        e.target.value = '';
        return;
      }
      const valid = [];
      const oversize = [];
      const maxSize = maxSizeBytes;
      for (let i = 0; i < newFiles.length && valid.length < remaining; i++) {
        if (newFiles[i].size > maxSize) {
          oversize.push(newFiles[i].name);
        } else {
          valid.push(newFiles[i]);
        }
      }
      if (oversize.length) {
        setInternalError(
          `Một số ảnh vượt dung lượng cho phép (tối đa ${formatBytes(maxSize)}): ${oversize.slice(0, 3).join(', ')}${oversize.length > 3 ? '...' : ''}`,
        );
      }
      const next = [...files, ...valid].slice(0, limit);
      onChange(next);
      e.target.value = '';
    },
    [files, maxCount, maxSizeBytes, onChange],
  );

  const handleRemove = useCallback(
    (index) => {
      const next = files.filter((_, i) => i !== index);
      onChange(next);
      setInternalError(null);
    },
    [files, onChange],
  );

  const handleDragEnd = useCallback(
    (event) => {
      const { active, over } = event;
      if (!over || active.id === over.id) return;
      const oldIndex = files.findIndex((_, i) => String(i) === active.id);
      const newIndex = files.findIndex((_, i) => String(i) === over.id);
      if (oldIndex === -1 || newIndex === -1) return;
      const next = arrayMove(files, oldIndex, newIndex);
      onChange(next);
    },
    [files, onChange],
  );

  const canAddMore = typeof maxCount === 'number' && files.length < maxCount;

  const triggerFileSelect = useCallback(() => {
    onAddButtonClick?.();
    if (resolvedInputRef?.current) resolvedInputRef.current.click();
  }, [resolvedInputRef, onAddButtonClick]);

  return (
    <div className={`multi-image-upload ${className || ''}`}>
      <input
        ref={resolvedInputRef}
        type="file"
        accept={accept}
        multiple
        onChange={handleFileChange}
        className="multi-image-upload__input-hidden"
        aria-hidden
        tabIndex={-1}
      />
      {label && <Form.Label className="d-block">{label}</Form.Label>}
      {hint && (
        <Form.Text className="d-block text-muted small mb-2">{hint}</Form.Text>
      )}
      {displayError && (
        <div className="multi-image-upload__error text-danger small mb-2">
          {displayError}
        </div>
      )}
      {(files.length > 0 || canAddMore) && (
        <div className="multi-image-upload__list">
          {files.length > 0 && (
            <DndContext
              sensors={sensors}
              collisionDetection={closestCenter}
              onDragEnd={handleDragEnd}
            >
              <SortableContext
                items={files.map((_, i) => String(i))}
                strategy={verticalListSortingStrategy}
              >
                {files.map((file, index) => (
                  <SortableImageItem
                    key={`${file.name}-${file.size}-${index}`}
                    file={file}
                    previewUrl={previewUrls[index] || ''}
                    index={index}
                    total={files.length}
                    firstImageLabel={firstImageLabel}
                    onRemove={handleRemove}
                  />
                ))}
              </SortableContext>
            </DndContext>
          )}
          {canAddMore && (
            <button
              type="button"
              className="multi-image-upload__add-btn"
              onClick={triggerFileSelect}
              aria-label={addButtonLabel}
            >
              <span className="multi-image-upload__add-icon">
                <FaPlus />
              </span>
              <span className="multi-image-upload__add-label">
                {addButtonLabel}
              </span>
            </button>
          )}
        </div>
      )}
      {files.length > 0 && reorderHint && (
        <p className="small text-muted mb-2 mt-2">{reorderHint}</p>
      )}
    </div>
  );
}

function formatBytes(bytes) {
  if (bytes >= 1024 * 1024) return `${bytes / (1024 * 1024)}MB`;
  if (bytes >= 1024) return `${bytes / 1024}KB`;
  return `${bytes}B`;
}

MultiImageUpload.propTypes = {
  value: PropTypes.arrayOf(PropTypes.instanceOf(File)),
  onChange: PropTypes.func.isRequired,
  maxCount: PropTypes.number.isRequired,
  maxSizeBytes: PropTypes.number.isRequired,
  accept: PropTypes.string,
  label: PropTypes.node,
  firstImageLabel: PropTypes.string,
  hint: PropTypes.string,
  reorderHint: PropTypes.string,
  addButtonLabel: PropTypes.string,
  validationError: PropTypes.string,
  onAddButtonClick: PropTypes.func,
  className: PropTypes.string,
  inputRef: PropTypes.object,
};

export default MultiImageUpload;
