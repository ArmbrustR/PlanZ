import {getProducts} from "../services/ProductApiService";
import {useEffect, useState} from "react";

export default function showProductsInTable () {
    const [productsArray, setProductsArray] = useState([])

    useEffect(() => {
        getProducts()
            .then(setProductsArray)
            .catch((error) => console.error(error))
    }, [])


    const rows = productsArray.map((product) => {
        return (
            <tr key={product.sku}>
                <td>{product.sku}</td>
                <td>{product.title}</td>
                <td>{product.itemDescription}</td>
            </tr>
        )
    })

    return (
        <table>
            <thead>
                <tr>
                    <th>image</th>
                    <th>asin</th>
                    <th>title</th>
                    <th>inventoryAlert</th>
                    <th>expectedSales</th>
                </tr>
            </thead>
            <tbody>
            {rows}
            </tbody>
        </table>
    )
}

