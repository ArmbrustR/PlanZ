import {getProductsByAsins} from "../services/ProductApiService";
import {useEffect, useState} from "react";
import styled from "styled-components/macro";
import SalesChart from "./SalesChart";
import InventoryChart from "./InventoryChart";


export default function ShowProductsInTable() {
    const [asinProductList, setAsinProductList] = useState([])

    useEffect(() => {
        getProductsByAsins()
            .then(setAsinProductList)
            .catch((error) => console.error(error))
    }, [])


    const sortChance = () => {
        setAsinProductList(
            [...asinProductList].sort(function (a, b) {
                return b.differenceFromExpectedSalesToActualSales - a.differenceFromExpectedSalesToActualSales;
            })
        )
    }

    const sortExpected = () => {
        setAsinProductList(
            [...asinProductList].sort(function (a, b) {
                return b.expectedSales - a.expectedSales;
            })
        )
    }

    const rows = asinProductList.map((product) => {
        return (
            <tr key={product.asin}>
                <td>{product.asin}</td>
                <td>{product.title}</td>
                <td>
                    <InventoryChart product={product}/>
                </td>
                <td>
                    <SalesChart product={product}/>
                </td>
                <td>{product.expectedSales}</td>
                <td>
                    {product.differenceFromExpectedSalesToActualSales}
                </td>
            </tr>
        )
    })

    return (
        <Wrapper>

            <table>
                <thead>
                <tr>
                    <th>Asin</th>
                    <th>Title</th>
                    <th>Inventory 30days</th>
                    <th>Sales 30days</th>
                    <th>
                        <button onClick={sortExpected}>Expected Sales</button>
                    </th>
                    <th>
                        <button onClick={sortChance}>Chance to sell more</button>
                    </th>
                </tr>
                </thead>
                <tbody>
                {rows}
                </tbody>
            </table>
        </Wrapper>
    )
}


const Wrapper = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: center;
  overflow-y: auto;
  box-shadow: #282c34;

  button {
    background: transparent;
    border-width: 0;
    font-size: 1em;
    font-weight: bold;
    font-family: "Open Sans";
    outline: none;
  }

  button:hover {
  }


  th {
    background-color: orange;
    height: 80px;
    font-size: 1em;
    font-weight: bold;
    font-family: "Open Sans";
  }

  td, th {
    border-style: dashed;
    border-width: 1px 0px 0px 0px;
    border-color: white;
    height: 80px;
    font-family: "Open Sans";
  }


  tr:hover {
    background-color: grey;
    color: white;
  }
`