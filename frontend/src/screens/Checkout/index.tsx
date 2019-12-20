import React, { useState, useEffect } from "react";
import {
  List,
  Divider,
  ListItem,
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
import { StripeProvider, Elements } from "react-stripe-elements";
import DeleteIcon from "@material-ui/icons/Delete";

import PaymentForm from "../../components/PaymentForm";
import Cart from "../../interfaces/Cart";
import Loading from "../../components/Loading";

export default function Checkout() {
  const [cart, setCart] = useState<Cart>({});
  const [isLoaded, setIsLoaded] = useState<boolean>(false);
  const [updated, setUpdated] = useState<number>(0);

  const NAME_MAX_LENGTH = 15;
  const DESCRIPTION_MAX_LENGTH = 29;

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

  let subTotal = 0;

  return !isLoaded ? (
    <Loading />
  ) : (
    <StripeProvider apiKey="pk_test_6pRNASCoBOKtIshFeQd4XMUh">
      <Container maxWidth="sm">
        <CssBaseline />
        <Paper className="MyPaper">
          <Typography variant="h4">Checkout</Typography>
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
                      primary={
                        product.name.length > NAME_MAX_LENGTH
                          ? `${product.name.substring(0, NAME_MAX_LENGTH)}...`
                          : product.name
                      }
                      secondary={
                        <>
                          <Typography
                            component="span"
                            variant="body2"
                            color="textPrimary"
                          >
                            {product.description.length > DESCRIPTION_MAX_LENGTH
                              ? `${product.description.substring(
                                  0,
                                  DESCRIPTION_MAX_LENGTH
                                )}...`
                              : product.description}
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
          <Typography variant="overline">Total: $HKD {subTotal}</Typography>
          <Divider />
          <Elements>
            <PaymentForm />
          </Elements>
        </Paper>
      </Container>
    </StripeProvider>
  );
}