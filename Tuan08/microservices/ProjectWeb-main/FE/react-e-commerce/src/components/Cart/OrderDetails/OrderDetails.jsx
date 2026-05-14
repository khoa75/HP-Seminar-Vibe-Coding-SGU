import "./OrderDetails.css";

import { useGlobalContext } from "@/components/GlobalContext/GlobalContext";

const OrderDetails = ({ product }) => {
  const {store} = useGlobalContext();
  return (
    <div className="order-details">
      <div className="order-detail">
        <div className="left-side">
          <img src={product.image} alt="" />
        </div>
        <div className="right-side">
          <h3>{product.name}</h3>
          <p>{product.description}</p>
        </div>
      </div>
      <div className="order-price">
        <h3>${product.price}</h3>
      </div>
      <div className="quantity">
        <p>Quantity</p>
        <div className="increase-quantity">
          <button
            onClick={() => {
              store.reduceQuantity(product.id);
            }}
            disabled={product.quantity <= 1}
          >
            -
          </button>
          <input
            type="number"
            min={1}
            value={product.quantity}
            onChange={(event) => {
              store.setQuantity(product.id, event.target.value);
            }}
          />
          <button
            onClick={() => {
              store.addQuantity(product.id);
            }}
          >
            +
          </button>
        </div>
      </div>
      <div className="remove">
        <button
          onClick={() => {
            store.removeFromCart(product?.id);
          }}
        >
          Remove
        </button>
      </div>
    </div>
  );
};
export default OrderDetails;
