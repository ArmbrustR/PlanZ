import axios from 'axios'

const productUrl = '/api/product'
const getProductsByAsinUrl = '/api/product/asins'
const getExpectedSalesByAsin = '/api/product/expected'


export const getProducts = () =>
    axios.get(productUrl).then((response) => response.data)

export const getProductsByAsins = () =>
    axios.get(getProductsByAsinUrl).then((response) => response.data)

export const getChanceToSellMore = (asin) =>
    axios.get(`${getExpectedSalesByAsin}/${asin}`).then((response) => response.data)
