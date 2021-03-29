import axios from 'axios'

const productUrl = '/api/product'


export const getProducts = () =>
    axios.get(productUrl).then((response) => response.data)

