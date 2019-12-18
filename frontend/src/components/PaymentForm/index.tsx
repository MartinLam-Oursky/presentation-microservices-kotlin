import React, { FormEvent } from "react";
import { CardElement, injectStripe } from "react-stripe-elements";

import "./PaymentForm.scss";

function PaymentForm() {
  const handleSubmit = (ev: FormEvent<HTMLFormElement>) => {
    ev.preventDefault();
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
      <button>Pay</button>
    </form>
  );
}

export default injectStripe(PaymentForm);
