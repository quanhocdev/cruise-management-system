import React from "react";
import "./../styles/TourCardSkeleton.css";

const TourCardSkeleton = () => {
  return (
    <div className="tour-card-skeleton">
      {/* Ảnh */}
      <div className="tour-card-skeleton__image skeleton-shimmer" />

      {/* Nội dung */}
      <div className="tour-card-skeleton__body">
        {/* Mã tour + trạng thái */}
        <div className="tour-card-skeleton__meta">
          <div className="skeleton-shimmer skeleton-line skeleton-line--code" />
          <div className="skeleton-shimmer skeleton-line skeleton-line--status" />
        </div>

        {/* Tên tour */}
        <div className="skeleton-shimmer skeleton-line skeleton-line--title" />
        <div className="skeleton-shimmer skeleton-line skeleton-line--title-short" />

        {/* Mô tả */}
        <div className="tour-card-skeleton__description">
          <div className="skeleton-shimmer skeleton-line" />
          <div className="skeleton-shimmer skeleton-line" />
          <div className="skeleton-shimmer skeleton-line skeleton-line--short" />
        </div>

        {/* Thông tin tour */}
        <div className="tour-card-skeleton__info">
          <div className="skeleton-shimmer skeleton-line" />
          <div className="skeleton-shimmer skeleton-line" />
          <div className="skeleton-shimmer skeleton-line skeleton-line--medium" />
        </div>

        {/* Giá + button */}
        <div className="tour-card-skeleton__footer">
          <div>
            <div className="skeleton-shimmer skeleton-line skeleton-line--price-label" />
            <div className="skeleton-shimmer skeleton-line skeleton-line--price" />
          </div>

          <div className="skeleton-shimmer skeleton-button" />
        </div>
      </div>
    </div>
  );
};

export default TourCardSkeleton;
