import React from "react";
import { Container, Paper, Typography, CssBaseline } from "@material-ui/core";
import { StripeProvider, Elements } from "react-stripe-elements";

import PaymentForm from "../../components/PaymentForm";

export default function Checkout() {
  return (
    <StripeProvider apiKey="pk_test_6pRNASCoBOKtIshFeQd4XMUh">
      <Container maxWidth="sm">
        <CssBaseline />
        <Paper className="MyPaper">
          <Typography variant="h5">Checkout</Typography>
          <br />
          <Elements>
            <PaymentForm />
          </Elements>
        </Paper>
      </Container>
    </StripeProvider>
  );
}
