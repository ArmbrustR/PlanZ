import * as React from 'react';
import {useEffect, useState} from 'react';
import {getProductsByAsins} from "../services/ProductApiService";
import {Table, TableBody, TableCell, TableHead, TableRow} from '@material-ui/core';
import InventoryChart from "./InventoryChart";
import SalesChart from "./SalesChart";

export default function ProductTableMui() {
    const [asinProductList, setAsinProductList] = useState([])

    useEffect(() => {
        getProductsByAsins()
            .then(setAsinProductList)
            .catch((error) => console.error(error))
    }, [])


    const columns = [
        {field: 'asin', headerName: 'Asin', sortable: false, width: 80},
        {field: 'title', headerName: 'Title', sortable: false, width: 250},
        {field: 'inventory', headerName: 'Inventory 30days', sortable: false, width: 200},
        {field: 'sales', headerName: 'Sales 30days', sortable: false, width: 200},
        {field: 'expected', headerName: 'Expected Sales', type: 'number', width: 200},
        {field: 'chance', headerName: 'Chance to sell more', type: 'number', width: 200},
    ];

    const rows = asinProductList.map((product) => {
        return (
            <TableRow key={product.asin}>
                <TableCell>{product.asin}</TableCell>
                <TableCell>{product.title} </TableCell>
                <TableCell><InventoryChart product={product}/></TableCell>
                <TableCell><SalesChart product={product}/></TableCell>
                <TableCell>{product.expectedSales}</TableCell>
                <TableCell>{product.differenceFromExpectedSalesToActualSales}</TableCell>
            </TableRow>
        )
    })

    return (
        <div style={{height: '100%', width: '100%'}}>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>Asin</TableCell>
                        <TableCell>Title</TableCell>
                        <TableCell>Inventory 30day</TableCell>
                        <TableCell>Sales 30days</TableCell>
                        <TableCell>Expected Sales</TableCell>
                        <TableCell>Chance to sell more</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {rows}
                </TableBody>
            </Table>

        </div>
    )
}
