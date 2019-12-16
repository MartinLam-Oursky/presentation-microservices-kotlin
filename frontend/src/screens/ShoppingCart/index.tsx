import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import {
  List,
  Divider,
  ListItem,
  Button,
  ListItemAvatar,
  ListItemText,
  Typography,
  Avatar,
  Container,
  ListItemSecondaryAction,
  IconButton,
  Paper,
  CssBaseline,
} from "@material-ui/core";

import Cart from "../../interfaces/Cart";
import DeleteIcon from "@material-ui/icons/Delete";
import Loading from "../../components/Loading";
import EmptyShoppingCart from "../EmptyShoppingCart";

import "./ShoppingCart.scss";

export default function ShoppingCart() {
  const [cart, setCart] = useState<Cart>({});
  const [isLoaded, setIsLoaded] = useState<boolean>(false);
  const [updated, setUpdated] = useState<number>(0);

  useEffect(() => {
    const storageCart = localStorage.getItem("cart");
    if (storageCart !== null && storageCart !== "") {
      setCart(JSON.parse(storageCart));
    }

    setIsLoaded(true);
  }, [updated]);

  function deleteProductFromCart(id: string) {
    console.log(cart[id]);
    delete cart[id];
    localStorage.setItem("cart", JSON.stringify(cart));
    setCart(cart);
    setUpdated(updated + 1);
  }

  function deleteAllProductFromCart() {
    localStorage.setItem("cart", "");
    setCart({});
  }

  let subTotal = 0;

  return !isLoaded ? (
    <Loading />
  ) : Object.keys(cart).length <= 0 ? (
    <EmptyShoppingCart />
  ) : (
    <Container maxWidth="sm">
      <Paper className="mypaper">
        <CssBaseline />

        <Typography variant="h4">Shopping Cart</Typography>

        <br />
        <Paper style={{ maxHeight: "400px", overflow: "auto" }}>
          <List>
            {Object.keys(cart).map((val: string) => {
              const product = cart[val];
              subTotal += product.price;

              return (
                <React.Fragment key={val}>
                  <ListItem alignItems="flex-start">
                    <ListItemAvatar>
                      {product.image && (
                        <Avatar
                          alt={product.name}
                          src={`http://localhost:9000/images/${product.image}`}
                        />
                      )}
                    </ListItemAvatar>
                    <ListItemText
                      primary={product.name}
                      secondary={
                        <>
                          <Typography
                            component="span"
                            variant="body2"
                            color="textPrimary"
                          >
                            {product.description}
                          </Typography>
                          <br />
                          $HKD {product.price}
                        </>
                      }
                    />
                    <ListItemSecondaryAction>
                      <IconButton
                        edge="end"
                        aria-label="delete"
                        onClick={() =>
                          deleteProductFromCart(product.id.toString())
                        }
                      >
                        <DeleteIcon />
                      </IconButton>
                    </ListItemSecondaryAction>
                  </ListItem>
                  <Divider variant="inset" component="li" />
                </React.Fragment>
              );
            })}
          </List>
        </Paper>
        <Typography variant="overline">Sub Total: $HKD {subTotal}</Typography>
        <br />
        <Button
          variant="contained"
          color="secondary"
          onClick={deleteAllProductFromCart}
          style={{ textAlign: "center" }}
        >
          Clear Shopping Cart
          <DeleteIcon />
        </Button>
        <br />
        <br />
        <Link to="/checkout">
          <Button
            variant="contained"
            color="primary"
            style={{ justifyContent: "center" }}
          >
            Checkout
          </Button>
        </Link>
      </Paper>
    </Container>
  );
}
