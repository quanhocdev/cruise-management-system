import React, { useEffect, useState } from "react";
import { Search, RefreshCw } from "lucide-react";
import useProduct from "../hooks/useProduct";
import ProductTable from "../components/ProductTable";

const ConvenienceProducts = () => {
  console.log("🔥 [PAGE] ConvenienceProducts RENDERED!");
  const { products, loading, error, loadProducts } = useProduct();
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    loadProducts();
  }, [loadProducts]);

  const filteredProducts = products.filter(
    (item) =>
      item.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.description?.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  return (
    <div className="convenience-page">
      <div className="page-header">
        <div>
          <h2 className="page-title">Danh sách Sản phẩm Tiện ích</h2>
          <p className="page-subtitle">
            Xem thông tin chi tiết và tồn kho các sản phẩm tiện ích
          </p>
        </div>
      </div>

      <div className="page-toolbar">
        <div className="search-box">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            placeholder="Tìm theo tên hoặc mô tả sản phẩm..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <button
          type="button"
          className="btn btn-secondary"
          onClick={() => loadProducts()}
          title="Làm mới"
        >
          <RefreshCw size={18} />
        </button>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="card">
        <ProductTable products={filteredProducts} loading={loading} />
      </div>
    </div>
  );
};

export default ConvenienceProducts;
