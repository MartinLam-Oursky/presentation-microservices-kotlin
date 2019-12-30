import React from "react";
import {
  TextField,
  Button,
  Typography,
  Container,
  Paper,
  CssBaseline,
} from "@material-ui/core";

export default function Login() {
  return (
    <Container maxWidth="xs">
      <CssBaseline />

      <Paper className="mypaper">
        <Typography variant="h4">Forgot Password</Typography>

        <br />

        <form>
          <TextField
            required={true}
            fullWidth={true}
            name="user"
            variant="outlined"
            label="Username"
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
