import React from "react";

import {
  Container,
  Paper,
  CssBaseline,
  CircularProgress,
} from "@material-ui/core";

export default function Loading() {
  return (
    <Container>
      <CssBaseline />
      <Paper className="mypaper">
        <CircularProgress />
      </Paper>
    </Container>
  );
}
