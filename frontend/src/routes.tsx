import React from "react";
import { Route, Router } from "react-router-dom";
import { createBrowserHistory } from "history";
import App from "./screens/Home";
import Navbar from "./components/Navbar";
import NewProductPage from "./screens/NewProduct";
import ShoppingCart from "./screens/ShoppingCart";
import Login from "./screens/Login";
import ForgotPassword from "./screens/ForgotPassword";
import Register from "./screens/Register";

const history = createBrowserHistory();

export default (
  <Router history={history}>
    <Navbar />
    <br />
    <Route path="/" exact={true} component={App} />
    <Route path="/new" exact={true} component={NewProductPage} />
    <Route path="/cart" exact={true} component={ShoppingCart} />
    <Route path="/login" exact={true} component={Login} />
    <Route path="/register" exact={true} component={Register} />
    <Route path="/forgot" exact={true} component={ForgotPassword} />
  </Router>
);
