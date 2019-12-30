import React, { useState, useEffect, useCallback } from "react";
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
import AlertDialog from "../../components/AlertDialog";
import { StripeProvider } from "react-stripe-elements";
import DeleteIcon from "@material-ui/icons/Delete";

import "./Checkout.scss";
import Cart from "../../interfaces/Cart";
import Loading from "../../components/Loading";

export default function Checkout() {
  const [cart, setCart] = useState<Cart>({});
  const [isLoaded, setIsLoaded] = useState<boolean>(false);
  const [updated, setUpdated] = useState<number>(0);
  const [checkoutMsg, setCheckoutMsg] = useState<string>("");

  const NAME_MAX_LENGTH = 15;
  const DESCRIPTION_MAX_LENGTH = 29;

  const setCheckoutMsgNull = useCallback(() => setCheckoutMsg(""), []);

  useEffect(() => {
    const storageCart = localStorage.getItem("cart");
    if (storageCart !== null && storageCart !== "") {
      setCart(JSON.parse(storageCart));
    }

    setIsLoaded(true);
  }, [updated]);

  function deleteProductFromCart(id: string) {
    delete cart[id];
    localStorage.setItem("cart", JSON.stringify(cart));
    setCart(cart);
    setUpdated(updated + 1);
  }

  const handleSubmit = () => {
    const items: Array<stripe.StripeCheckoutItem> = [];
    const cartKys = Object.keys(cart);
    if (cartKys.length === 0) {
      setCheckoutMsg("Your shopping cart is empty.");
      return;
    }
    cartKys.forEach((v: string) => {
      items.push({
        sku: cart[v].stripeSKUID,
        quantity: 1,
      });
    });
    window
      .Stripe("pk_test_zhH6ESOFrEJllkw6M7rt99EX00ESkJjbGs")
      .redirectToCheckout({
        items: items,
        successUrl: "http://localhost:3000?success=true",
        cancelUrl: "http://localhost:3000?cancelled=true",
        billingAddressCollection: "required",
      })
      .then((a: stripe.StripeRedirectResponse) => {
        console.log("Done: ", a);
      })
      .catch((e: Error) => {
        console.log("Error in Stripe", e);
        setCheckoutMsg(e.message);
      });
  };

  let subTotal = 0;

  return !isLoaded ? (
    <Loading />
  ) : (
    <StripeProvider apiKey="pk_test_6pRNASCoBOKtIshFeQd4XMUh">
      <Container maxWidth="sm">
        <CssBaseline />

        <AlertDialog
          redirectTo={undefined}
          title="Error"
          message={checkoutMsg}
          isOpen={!!checkoutMsg}
          onCloseClick={setCheckoutMsgNull}
        />

        <Paper className="mypaper">
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
          <button onClick={handleSubmit} className="payBtn">
            Pay
          </button>
        </Paper>
      </Container>
    </StripeProvider>
  );
}
