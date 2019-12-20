export default interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  image: string;
  ownerID: number;
  enabled: boolean;
  stripeProductID: string;
  stripeSKUID: string;
}
