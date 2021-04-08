import {Chart} from 'react-charts'
import React from "react";
import styled from "styled-components/macro";

export default function SalesChart({product}) {

    const productSales = product.sales;

    const updatedSalesForChart = productSales.map((sale) => {
        if (sale.quantity === 0) {
            sale.quantity = 0.01
            return sale
        }
        return sale
    })

    const data = React.useMemo(
        () => [
            {
                label: 'Series 1',
                data: updatedSalesForChart.map((sale) => ({
                    primary: sale.date,
                    secondary: sale.quantity
                }))
            }], [product.sales]
    )

    const axes = React.useMemo(
        () => [
            {primary: true, type: "ordinal", position: "bottom", show: false},
            {position: "left", type: "linear", show: false}
        ], []
    )

    if (product.sales.length === 0) {
        return null
    }

    return (
        <Wrapper>
            <Chart className="chart" data={data} axes={axes} tooltip/>
        </Wrapper>
    )

}

const Wrapper = styled.div`
  position: static;
  height: 60px;
  width: 200px;
  border-width: 0px 0px 1px 0px;
  border-style: solid;
  border-color: gray;

  > div > div {
    z-index: 101;
  }

`