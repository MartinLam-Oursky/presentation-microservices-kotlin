import React, { FormEvent } from "react";
import {
  CardElement,
  injectStripe,
  ReactStripeElements,
} from "react-stripe-elements";

import "./PaymentForm.scss";

function PaymentForm(props: ReactStripeElements.InjectedStripeProps) {
  const handleSubmit = (ev: FormEvent<HTMLFormElement>) => {
    ev.preventDefault();
    if (props.stripe) {
      props.stripe
        .createToken()
        .then((payload: ReactStripeElements.PatchedTokenResponse) => {
          if (payload.error) {
            alert(payload.error.message);
          } else {
            console.log(payload.token);
          }
        })
        .catch((err: Error) => alert(err));
    } else {
      console.log("Stripe.js hasn't loaded yet.");
    }
  };

  const createOptions = (fontSize: string) => {
    return {
      style: {
        base: {
          fontSize,
          color: "#424770",
          letterSpacing: "0.025em",
          fontFamily: "Source Code Pro, monospace",
          "::placeholder": {
            color: "#aab7c4",
          },
          padding: "0px",
        },
        invalid: {
          color: "#9e2146",
        },
      },
    };
  };

  return (
    <form onSubmit={handleSubmit}>
      <label>
        <CardElement {...createOptions("13px")} />
      </label>
      <button type="submit">Pay</button>
    </form>
  );
}

export default injectStripe(PaymentForm);
