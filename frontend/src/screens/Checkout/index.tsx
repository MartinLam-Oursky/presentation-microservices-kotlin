import React from "react";
import { Container, Paper, Typography, CssBaseline } from "@material-ui/core";

export default function Checkout() {
  return (
    <Container maxWidth="sm">
      <CssBaseline />
      <Paper className="MyPaper">
        <Typography variant="h5" component="h5">
          Checkout Page
        </Typography>
      </Paper>
    </Container>
  );
}
