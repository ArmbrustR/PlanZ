import axios from 'axios'

const getProductsByAsinUrl = '/api/product/asins'

export const getProductsByAsins = () =>
    axios.get(getProductsByAsinUrl).then((response) => response.data)
