import React, { FormEvent, useState, useCallback } from "react";
import APIService from "../../APIService";
import {
  TextField,
  Button,
  Typography,
  Container,
  Paper,
  CssBaseline,
} from "@material-ui/core";
import AlertDialog from "../../components/AlertDialog";
import Cookies from "../../Cookies";
import { TIME, getTimeAfter } from "../../Utils";
import RegisterData from "../../interfaces/RegisterData";
import { RegisterResult } from "../../interfaces/APIResponses";

export default function Register() {
  const [submitResult, setSubmitResult] = useState<string>("");
  const [success, setSuccess] = useState<boolean>(false);
  const [registerData, setRegisterData] = useState<RegisterData>({
    email: "",
    pass: "",
    confpass: "",
  });
  const [isMerchant, setIsMerchant] = useState<boolean>(false);

  const handleChange = (name: keyof RegisterData) => (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setRegisterData({ ...registerData, [name]: event.target.value });
  };

  const setSubmitResultEmpty = useCallback(() => setSubmitResult(""), []);
  const updateIsMerchant = useCallback(() => setIsMerchant(!isMerchant), [
    isMerchant,
  ]);

  function submitForm(e: FormEvent<HTMLFormElement>): void {
    e.preventDefault();
    if (registerData.confpass !== registerData.pass) {
      setSubmitResult("Passwords are not the same !");
      return;
    }

    APIService.Auth.register(registerData, isMerchant)
      .then((result: RegisterResult) => {
        if (result.error) {
          setSubmitResult(result.error);
          setSuccess(false);
        } else {
          setSubmitResult("Successfully Registered.");
          Cookies.setCookie(
            "accessToken",
            result.accessToken || "",
            getTimeAfter(TIME.TEN_MINUTE)
          );
          Cookies.setCookie(
            "isMerchant",
            isMerchant.toString(),
            getTimeAfter(TIME.TEN_MINUTE)
          );
          localStorage.setItem("userId", (result.userId || "").toString());
          setSuccess(true);
        }
      })
      .catch(e => {
        setSubmitResult(`Error: ${e}`);
        setSuccess(false);
      });
  }

  return (
    <Container maxWidth="xs" component="main">
      <CssBaseline />

      <AlertDialog
        redirectTo={success ? "/" : undefined}
        title="Registeration Result"
        message={submitResult}
        isOpen={!!submitResult}
        onCloseClick={setSubmitResultEmpty}
      />

      <Paper className="mypaper">
        <Typography variant="h4">
          {isMerchant ? "Merchant" : "Customer"} Register
        </Typography>

        <br />

        <form onSubmit={submitForm}>
          <TextField
            required={true}
            fullWidth={true}
            type="email"
            name="email"
            variant="outlined"
            label="Email"
            onChange={handleChange("email")}
          />

          <br />
          <br />

          <TextField
            fullWidth={true}
            required={true}
            name="pass"
            type="password"
            variant="outlined"
            label="Password"
            onChange={handleChange("pass")}
          />

          <br />
          <br />

          <TextField
            fullWidth={true}
            required={true}
            name="confpass"
            type="password"
            variant="outlined"
            label="Confirm Password"
            onChange={handleChange("confpass")}
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
          <br />
          <Button color="primary" onClick={updateIsMerchant}>
            <Typography variant="caption">
              Register as {isMerchant ? "Customer" : "Merchant"} ?
            </Typography>
          </Button>
        </form>
      </Paper>
    </Container>
  );
}
