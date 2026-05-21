import Product from "../Product/Product";

import "./TopProducts.css";
import { useGlobalContext } from "@/components/GlobalContext/GlobalContext";
import Skeleton from "react-loading-skeleton";

const TopProducts = () => {
  let {store} = useGlobalContext();
  // return from highest to lowest using times_bought

  const topProducts = [...store.state.products]
    .sort((a, b) => b.times_bought - a.times_bought)
    .slice(0, 6);
  return (
    <div className="sub-container">
      <h2>Top Sellers!</h2>
      <div className="contains-product">
        {store.state.products.length > 0 ? (
          topProducts.map((product) => {
            return <Product key={product.id} product={product}></Product>;
          })
        ) : (
          <div className="skeleton">
            <Skeleton height={250}></Skeleton>
          </div>
        )}
      </div>
    </div>
  );
};
export default TopProducts;
