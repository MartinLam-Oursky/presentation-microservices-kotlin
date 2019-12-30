import React, { useState, useEffect, useCallback } from "react";

import {
  Card,
  CardActionArea,
  CardActions,
  CardContent,
  CardMedia,
  Button,
  Typography,
  Container,
  Paper,
  CssBaseline,
  Grid,
} from "@material-ui/core";

import AlertDialog from "../../components/AlertDialog";
import Loading from "../../components/Loading";
import Product from "../../interfaces/Product";
import Cart from "../../interfaces/Cart";

import AddShoppingCartIcon from "@material-ui/icons/AddShoppingCartOutlined";
import "./Home.scss";
import APIService from "../../APIService";
import { AlertDialogProps } from "../../interfaces/DialogsProps";

export default function App() {
  const [isBiggerMode, setIsBiggerMode] = useState<boolean>(false);
  const [isLoaded, setIsLoaded] = useState<boolean>(false);
  const [items, setItems] = useState<Product[]>([]);
  const [cart, setCart] = useState<Cart>({});
  const [alertProps, setAlertProps] = useState<AlertDialogProps | undefined>();
  const updateStorageCart = useCallback(() => {
    const storageCart = localStorage.getItem("cart");
    if (storageCart !== null && storageCart !== "") {
      setCart(JSON.parse(storageCart));
    }
  }, []);

  const getDescriptionMaxTextLength = useCallback(
    () => (isBiggerMode ? 40 : 29),
    [isBiggerMode]
  );
  const getNameMaxTextLength = useCallback(() => (isBiggerMode ? 25 : 15), [
    isBiggerMode,
  ]);

  const toggleBiggerMode = useCallback(() => {
    setIsBiggerMode(!isBiggerMode);
  }, [isBiggerMode]);

  const closePopup = useCallback(() => {
    setAlertProps(undefined);
  }, []);

  const updateStateItems = useCallback(() => {
    APIService.Products.list()
      .then(result => {
        setIsLoaded(true);
        setItems(result);
      })
      .catch(_ => {
        setIsLoaded(true);
        setAlertProps({
          title: "Error",
          message:
            "Error occurred while getting products. Please make sure you have internet connection.",
          isOpen: true,
          onCloseClick: closePopup,
          redirectTo: undefined,
        });
      });
  }, [closePopup]);

  useEffect(() => {
    updateStateItems();
    updateStorageCart();
  }, [updateStateItems, updateStorageCart]);

  const addProductToCart = useCallback(
    (product: Product) => () => {
      cart[product.id.toString()] = product;
      setCart(cart);
      localStorage.setItem("cart", JSON.stringify(cart));
      setAlertProps({
        title: "Successful",
        message: `Added ${cart[product.id.toString()].name} to shopping cart.`,
        isOpen: true,
        onCloseClick: closePopup,
        redirectTo: undefined,
      });
    },
    [setAlertProps, setCart, cart, closePopup]
  );

  return !isLoaded ? (
    <Loading />
  ) : (
    <Container>
      <CssBaseline />
      <Typography align="right">
        Items:
        <Button onClick={toggleBiggerMode} disabled={isBiggerMode}>
          3
        </Button>
        |
        <Button onClick={toggleBiggerMode} disabled={!isBiggerMode}>
          4
        </Button>
      </Typography>

      <Paper className="mypaper">
        {alertProps && (
          <AlertDialog
            title={alertProps.title}
            message={alertProps.message}
            isOpen={alertProps.isOpen}
            onCloseClick={alertProps.onCloseClick}
            redirectTo={alertProps.redirectTo}
          />
        )}

        <br />
        {items != null ? (
          <Grid
            container={true}
            direction="row"
            justify="space-evenly"
            alignItems="center"
          >
            {items.map((item: Product) => (
              <Grid item={true} key={item.id}>
                <Card className={isBiggerMode ? "biggercard" : "mycard"}>
                  <CardActionArea>
                    <CardMedia
                      image={
                        item.image
                          ? "http://localhost:9000/images/" + item.image
                          : "//cdn.shopify.com/s/files/1/0533/2089/files/placeholder-images-image_large.png"
                      }
                      style={{ height: 140 }}
                    />
                    <CardContent>
                      <Typography
                        gutterBottom={true}
                        variant="h5"
                        component="h2"
                      >
                        {item.name.length > getNameMaxTextLength()
                          ? `${item.name.substring(
                              0,
                              getNameMaxTextLength()
                            )}...`
                          : item.name}
                      </Typography>
                      <Typography
                        variant="body2"
                        color="textSecondary"
                        component="p"
                      >
                        {item.description.length > getDescriptionMaxTextLength()
                          ? `${item.description.substring(
                              0,
                              getDescriptionMaxTextLength()
                            )}...`
                          : item.description}
                      </Typography>
                      <Typography
                        variant="body2"
                        color="textSecondary"
                        component="p"
                      >
                        ${item.price}
                      </Typography>
                    </CardContent>
                  </CardActionArea>
                  <CardActions>
                    <Button
                      size="small"
                      color="primary"
                      onClick={addProductToCart(item)}
                    >
                      <AddShoppingCartIcon /> Add to Cart
                    </Button>
                  </CardActions>
                </Card>
              </Grid>
            ))}
          </Grid>
        ) : (
          <Typography variant="h5" component="h5">
            No Products available now.
          </Typography>
        )}
      </Paper>
    </Container>
  );
}
