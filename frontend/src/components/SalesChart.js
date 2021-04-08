import {Chart} from 'react-charts'
import React from "react";
import styled from "styled-components/macro";

export default function SalesChart({product}) {

    const data = React.useMemo(
        () => [
            {
                label: 'Series 1',
                data: product.sales.map((sale) => ({
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

    return (
        <Wrapper>
            <Chart data={data} axes={axes} tooltip/>
        </Wrapper>
    )

}

const Wrapper = styled.div`
  height: 60px;
  width: 200px;
  border-width: 0px 0px 1px 0px;
  border-style: solid;
  border-color: gray;
  box-shadow: #61dafb;
`