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
import ConfirmDialog from "../../components/ConfirmDialog";
import UpdateProductDialog from "../../components/UpdateProductDialog";
import Loading from "../../components/Loading";
import Product from "../../interfaces/Product";
import DeleteForeverIcon from "@material-ui/icons/DeleteForeverOutlined";
import UpdateIcon from "@material-ui/icons/UpdateOutlined";
import APIService from "../../APIService";
import {
  AlertDialogProps,
  ConfirmDialogProps,
  UpdateProductDialogProps,
} from "../../interfaces/DialogsProps";
import Cookies from "../../Cookies";
import NewProductData from "../../interfaces/NewProductData";

export default function ManageProduct() {
  const [isLoaded, setIsLoaded] = useState<boolean>(false);
  const [items, setItems] = useState<Product[]>([]);
  const [alertProps, setAlertProps] = useState<AlertDialogProps | undefined>();
  const [updateProductProps, setUpdateProductProps] = useState<
    UpdateProductDialogProps | undefined
  >();
  const [confirmProps, setConfirmProps] = useState<
    ConfirmDialogProps | undefined
  >({
    title: "",
    isOpen: false,
    onOk: () => {},
    onClose: () => {},
    message: "",
  });
  const userid = parseInt(localStorage.getItem("userId") || "-1", 10);
  const closePopup = useCallback(() => {
    setAlertProps(undefined);
  }, []);
  const closeUpdateForm = useCallback(
    () => setUpdateProductProps(undefined),
    []
  );

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

  const checkIsMerchant = useCallback(() => {
    if (Cookies.getCookie("isMerchant") !== "true") {
      setAlertProps({
        title: "Unauthorized",
        message: "Only Merchant can access this page.",
        isOpen: true,
        onCloseClick: closePopup,
        redirectTo: "/",
      });
    }
  }, [closePopup]);

  useEffect(() => {
    checkIsMerchant();
    updateStateItems();
  }, [updateStateItems, checkIsMerchant]);

  const closeConfirmDialog = useCallback(() => {
    setConfirmProps(undefined);
  }, []);

  const onConfirmDeleteProduct = useCallback(
    (id: number) => async () => {
      try {
        const token = Cookies.getCookie("accessToken");
        if (token === null) {
          closeConfirmDialog();
          setAlertProps({
            title: "Incorrect access token",
            message: "Please login first.",
            isOpen: true,
            onCloseClick: closePopup,
            redirectTo: "/login",
          });
          return;
        }
        const result = await APIService.Products.delete(id, token);
        if (result.error) {
          closeConfirmDialog();
          setAlertProps({
            title: "Delete Product Result",
            message: result.error,
            isOpen: true,
            onCloseClick: closePopup,
            redirectTo: undefined,
          });
          return;
        }
        updateStateItems();
        closeConfirmDialog();
        setAlertProps({
          title: "Delete Product Result",
          message: result ? "Product Deleted." : "Failed to delete product.",
          isOpen: true,
          onCloseClick: closePopup,
          redirectTo: undefined,
        });
      } catch (e) {
        console.error("Error in deleting product: ", e);
        closeConfirmDialog();
        setAlertProps({
          title: "[Error] Delete Product",
          message: e,
          isOpen: true,
          onCloseClick: closePopup,
          redirectTo: undefined,
        });
      }
    },
    [updateStateItems, setAlertProps, closePopup, closeConfirmDialog]
  );

  const submitUpdate = useCallback(
    (id: number, updatedProduct: NewProductData) => () => {
      const token = Cookies.getCookie("accessToken");
      if (token === null) {
        setAlertProps({
          title: "Incorrect access token",
          message: "Please login first.",
          isOpen: true,
          onCloseClick: closePopup,
          redirectTo: "/login",
        });
        return;
      }
      console.log(id, updatedProduct);
      APIService.Products.update(id, updatedProduct, token)
        .then(r => {
          console.log("Result: ", r);
          closeUpdateForm();
          setAlertProps({
            title: "Update Product Result",
            message: "Product Updated !",
            isOpen: true,
            onCloseClick: closePopup,
            redirectTo: undefined,
          });
          updateStateItems();
        })
        .catch(e => {
          console.log("Error: ", e);
          closeUpdateForm();
          setAlertProps({
            title: "Update Product Result",
            message: "Error updating Product." + e,
            isOpen: true,
            onCloseClick: closePopup,
            redirectTo: undefined,
          });
        });
    },
    [closePopup, closeUpdateForm, updateStateItems]
  );

  const updateProduct = useCallback(
    (item: Product) => () => {
      setUpdateProductProps({
        product: item,
        isOpen: true,
        onClose: closeUpdateForm,
        onSubmit: submitUpdate,
      });
    },
    [closeUpdateForm, submitUpdate]
  );

  const deleteProduct = useCallback(
    (id: number) => () => {
      setConfirmProps({
        title: "Confirmation",
        message: "Are you sure you want to DELETE this product ?",
        onClose: closeConfirmDialog,
        isOpen: true,
        onOk: onConfirmDeleteProduct(id),
      });
    },
    [setConfirmProps, closeConfirmDialog, onConfirmDeleteProduct]
  );

  return !isLoaded ? (
    <Loading />
  ) : (
    <Container>
      <CssBaseline />

      <Paper className="MyPaper">
        {alertProps && (
          <AlertDialog
            title={alertProps.title}
            message={alertProps.message}
            isOpen={alertProps.isOpen}
            onCloseClick={alertProps.onCloseClick}
            redirectTo={alertProps.redirectTo}
          />
        )}

        {confirmProps && (
          <ConfirmDialog
            title={confirmProps.title}
            message={confirmProps.message}
            isOpen={confirmProps.isOpen}
            onOk={confirmProps.onOk}
            onClose={confirmProps.onClose}
          />
        )}

        {updateProductProps && (
          <UpdateProductDialog
            product={updateProductProps.product}
            isOpen={updateProductProps.isOpen}
            onClose={updateProductProps.onClose}
            onSubmit={updateProductProps.onSubmit}
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
            {items.map(
              (item: Product) =>
                userid === item.ownerID && (
                  <Grid item={true} key={item.id}>
                    <Card className="myCard">
                      <CardActionArea>
                        {item.image && (
                          <CardMedia
                            image={"http://localhost:9000/images/" + item.image}
                            style={{ height: 140 }}
                          />
                        )}
                        <CardContent>
                          <Typography
                            gutterBottom={true}
                            variant="h5"
                            component="h2"
                          >
                            {item.name}
                          </Typography>
                          <Typography
                            variant="body2"
                            color="textSecondary"
                            component="p"
                          >
                            {item.description}
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
                          color="secondary"
                          onClick={deleteProduct(item.id)}
                        >
                          <DeleteForeverIcon /> Delete Product
                        </Button>

                        <Button
                          size="small"
                          color="primary"
                          onClick={updateProduct(item)}
                        >
                          <UpdateIcon /> Update Product
                        </Button>
                      </CardActions>
                    </Card>
                  </Grid>
                )
            )}
          </Grid>
        ) : (
          <Typography variant="h5" component="h5">
            You have no product
          </Typography>
        )}
      </Paper>
    </Container>
  );
}
