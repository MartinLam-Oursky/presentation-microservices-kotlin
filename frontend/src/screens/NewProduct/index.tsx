import React, { useEffect, useState, FormEvent, useCallback } from "react";
import APIService from "../../APIService";
import {
  TextField,
  InputAdornment,
  Button,
  Typography,
  CssBaseline,
  Container,
  Paper,
} from "@material-ui/core";
import AlertDialog from "../../components/AlertDialog";
import { AlertDialogProps } from "../../interfaces/DialogsProps";
import { AddProductResult } from "../../interfaces/APIResponses";
import NewProductData from "../../interfaces/NewProductData";
import Cookies from "../../Cookies";

export default function NewProduct() {
  const [alertProps, setAlertProps] = useState<AlertDialogProps | undefined>();
  const [newProductData, setNewProductData] = useState<NewProductData>({
    name: "",
    description: "",
    price: 0,
    files: "",
  });

  const closePopup = useCallback(() => {
    setAlertProps(undefined);
  }, []);

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

  const handleChange = (name: keyof NewProductData) => (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setNewProductData({ ...newProductData, [name]: event.target.value });
  };

  useEffect(() => {
    checkIsMerchant();
  });

  function submitForm(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const formPrice = newProductData.price;

    if (typeof formPrice !== "string") {
      setAlertProps({
        title: "Error",
        message: "Incorrect Product Price !",
        isOpen: true,
        onCloseClick: closePopup,
        redirectTo: undefined,
      });
      return;
    }

    const token = Cookies.getCookie("accessToken");
    if (token === null) {
      setAlertProps({
        title: "Unauthorized",
        message: "Please Login First",
        isOpen: true,
        onCloseClick: closePopup,
        redirectTo: "/login",
      });
      return;
    }

    const price = parseFloat(formPrice);
    if (isNaN(price)) {
      setAlertProps({
        title: "Error",
        message: "Price is not a number",
        isOpen: true,
        onCloseClick: closePopup,
        redirectTo: undefined,
      });
      return;
    }

    const target = document.querySelector("form");
    if (!target) return;

    APIService.Products.add(new FormData(target), token)
      .then((result: AddProductResult) => {
        if (result.error) {
          setAlertProps({
            title: "Add Product Result",
            message: result.error,
            isOpen: true,
            onCloseClick: closePopup,
            redirectTo: undefined,
          });
          return;
        }
        setAlertProps({
          title: "Add Product Result",
          message: `New Prodcut Added !\nProduct ID: ${result.productId}`,
          isOpen: true,
          onCloseClick: closePopup,
          redirectTo: "/",
        });
      })
      .catch(err => {
        setAlertProps({
          title: "Add Product Result",
          message: `Error: ${err}`,
          isOpen: true,
          onCloseClick: closePopup,
          redirectTo: undefined,
        });
      });
  }

  return (
    <Container maxWidth="xs">
      <CssBaseline />

      {alertProps && (
        <AlertDialog
          title={alertProps.title}
          message={alertProps.message}
          isOpen={alertProps.isOpen}
          onCloseClick={alertProps.onCloseClick}
          redirectTo={alertProps.redirectTo}
        />
      )}

      <Paper className="mypaper">
        <Typography variant="h4">Add New Product</Typography>

        <br />

        <form onSubmit={submitForm}>
          <TextField
            required={true}
            fullWidth={true}
            id="productName"
            name="name"
            variant="outlined"
            label="Product Name"
            onChange={handleChange("name")}
          />

          <br />
          <br />

          <TextField
            fullWidth={true}
            id="productDescription"
            name="description"
            variant="outlined"
            label="Product Description"
            onChange={handleChange("description")}
          />

          <br />
          <br />

          <TextField
            required={true}
            fullWidth={true}
            id="productPrice"
            name="price"
            label="Product Price"
            variant="outlined"
            onChange={handleChange("price")}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">$</InputAdornment>
              ),
            }}
          />

          <br />
          <br />

          <input
            type="file"
            name="files"
            id="raised-button-file"
            accept="image/*"
            onChange={handleChange("files")}
          />

          <br />
          <br />

          <Button
            fullWidth={true}
            variant="outlined"
            color="primary"
            type="submit"
          >
            Submit
          </Button>
        </form>
      </Paper>
    </Container>
  );
}
