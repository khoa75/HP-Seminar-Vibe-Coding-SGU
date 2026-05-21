import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { FaStar } from "react-icons/fa";
import { useGlobalContext } from "@/components/GlobalContext/GlobalContext";
import apiBaseUrl from "@/helpers/apiBase";
import headphonesPink from "@/assets/images/airpods_max_pink.jpg";
import "./ProductDetailView.css";

const ProductDetailView = () => {
  const { id } = useParams();
  const { store } = useGlobalContext();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [quantity, setQuantity] = useState(1);

  const rating = useMemo(() => {
    if (!product) return 0;
    return typeof product.rating === "number" ? product.rating : 4;
  }, [product]);

  useEffect(() => {
    let active = true;

    const existing = store.state.products.find((item) => item.id === id);
    if (existing) {
      setProduct(existing);
      setLoading(false);
      return undefined;
    }

    const load = async () => {
      setLoading(true);
      setError("");
      try {
        const response = await fetch(`${apiBaseUrl}/products/${id}`);
        if (!response.ok) {
          throw new Error("Failed to load product");
        }
        const data = await response.json();
        if (active) {
          setProduct(data);
        }
      } catch (err) {
        if (active) {
          setError("Unable to load this product right now.");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    load();
    return () => {
      active = false;
    };
  }, [id, store.state.products]);

  const isInCart = useMemo(() => {
    return store.state.cart.some((item) => item.id === product?.id);
  }, [store.state.cart, product]);

  const cartItem = useMemo(() => {
    return store.state.cart.find((item) => item.id === product?.id) || null;
  }, [store.state.cart, product]);

  const price = product?.price ?? 0;
  const strikePrice = price + 1285;
  const stockStatus = typeof product?.quantity === "number" && product.quantity > 0
    ? "In stock"
    : "Out of stock";

  const relatedProducts = useMemo(() => {
    return store.state.products
      .filter((item) => item.id !== id)
      .slice(0, 4);
  }, [store.state.products, id]);

  useEffect(() => {
    if (cartItem?.quantity) {
      setQuantity(cartItem.quantity);
    } else {
      setQuantity(1);
    }
  }, [cartItem]);

  if (loading) {
    return (
      <div className="product-detail-page">
        <div className="product-detail-shell">
          <div className="product-detail-loading">Loading product...</div>
        </div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="product-detail-page">
        <div className="product-detail-shell">
          <div className="product-detail-error">{error || "Product not found."}</div>
          <Link className="product-detail-back" to="/">Back to shop</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="product-detail-page">
      <div className="product-detail-glow" />
      <div className="product-detail-shell">
        <div className="product-detail-breadcrumb">
          <Link to="/">Home</Link>
          <span>/</span>
          <span className="crumb-active">Product</span>
        </div>

        <div className="product-detail-grid">
          <div className="product-detail-media">
            <div className="product-detail-image">
              <img
                src={product.image || headphonesPink}
                alt={product.name}
              />
            </div>
            <div className="product-detail-badges">
              <span className="badge">Fast delivery</span>
              <span className="badge">Authentic</span>
              <span className="badge">Easy returns</span>
            </div>
          </div>

          <div className="product-detail-info">
            <p className="product-detail-category">Product code {product.productCode || "N/A"}</p>
            <h1 className="product-detail-title">{product.name}</h1>
            <div className="product-detail-rating">
              <div className="stars">
                {Array.from({ length: rating }).map((_, idx) => (
                  <FaStar key={`star-${idx}`} />
                ))}
              </div>
              <span className="rating-note">{rating}.0</span>
            </div>

            <p className="product-detail-description">
              {product.description ||
                "A refined companion for your everyday listening. Crafted for comfort, tuned for clarity, and styled for modern life."}
            </p>

            <div className="product-detail-price">
              <span className="price-main">₹{price.toLocaleString()}</span>
              <span className="price-strike">₹{strikePrice.toLocaleString()}</span>
              <span className={`stock ${stockStatus === "In stock" ? "in" : "out"}`}>
                {stockStatus}
              </span>
            </div>

            <div className="product-detail-quantity">
              <span>Quantity</span>
              <div className="quantity-control">
                <button
                  type="button"
                  onClick={() => setQuantity((prev) => Math.max(1, prev - 1))}
                  disabled={quantity <= 1}
                >
                  -
                </button>
                <input
                  type="number"
                  min={1}
                  value={quantity}
                  onChange={(event) => {
                    const nextValue = Math.max(1, Number(event.target.value) || 1);
                    setQuantity(nextValue);
                  }}
                />
                <button
                  type="button"
                  onClick={() => setQuantity((prev) => prev + 1)}
                >
                  +
                </button>
              </div>
            </div>

            <div className="product-detail-actions">
              <button
                className={`primary-action ${isInCart ? "remove" : "add"}`}
                onClick={() => {
                  if (!isInCart) {
                    if (store.state.cartQuantity + quantity > 10) {
                      return;
                    }
                    store.addToCart(product.id, quantity);
                  } else {
                    store.removeFromCart(product.id);
                  }
                }}
                disabled={stockStatus !== "In stock"}
              >
                {isInCart ? "Remove from cart" : "Add to cart"}
              </button>
              <Link className="ghost-action" to="/cart">
                View cart
              </Link>
            </div>

            <div className="product-detail-meta">
              <div>
                <p className="meta-title">Delivery</p>
                <p>Standard: 2-4 days</p>
              </div>
              <div>
                <p className="meta-title">Warranty</p>
                <p>12 months official</p>
              </div>
              <div>
                <p className="meta-title">Support</p>
                <p>24/7 audio concierge</p>
              </div>
            </div>
          </div>
        </div>

        {relatedProducts.length > 0 && (
          <div className="product-detail-related">
            <h2>You might also like</h2>
            <div className="related-grid">
              {relatedProducts.map((item) => (
                <Link key={item.id} to={`/product/${item.id}`} className="related-card">
                  <img src={item.image || headphonesPink} alt={item.name} />
                  <div>
                    <p className="related-name">{item.name}</p>
                    <span className="related-price">₹{(item.price || 0).toLocaleString()}</span>
                  </div>
                </Link>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProductDetailView;
