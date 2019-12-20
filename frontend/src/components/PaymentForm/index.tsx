import React from "react";
import { injectStripe, ReactStripeElements } from "react-stripe-elements";

import "./PaymentForm.scss";

function PaymentForm(props: ReactStripeElements.InjectedStripeProps) {
  const handleSubmit = () => {
    if (props.stripe) {
      window
        .Stripe("pk_test_zhH6ESOFrEJllkw6M7rt99EX00ESkJjbGs")
        .redirectToCheckout({
          items: [
            {
              sku: "sku_GOPcNBWwZofkDr",
              quantity: 1,
            },
          ],
          successUrl: "http://localhost:3000/checkout",
          cancelUrl: "http://localhost:3000/checkout",
        })
        .then((a: stripe.StripeRedirectResponse) => {
          console.log("Done: ", a);
        })
        .catch((e: Error) => {
          console.log("EEERRRR", e);
        });
    } else {
      console.log("Stripe.js hasn't loaded yet.");
    }
  };

  return (
    <button onClick={handleSubmit} className="payBtn">
      Pay
    </button>
  );
}

export default injectStripe(PaymentForm);
