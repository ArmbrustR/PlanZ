import {Chart} from 'react-charts'
import React from 'react'
import styled from 'styled-components/macro';

export default function InventoryChart({product}) {

    const data = React.useMemo(
        () => [
            {
                label: 'Series 1',
                data: product.inventory.map((item) => ({
                    primary: item.date,
                    secondary: item.amount,
                }))
            }], [product]
    )

    const axes = React.useMemo(
        () => [
            {primary: true, type: "ordinal", position: "bottom", show: false},
            {position: "left", type: "linear", stacked: true, show: false, hardMax: 1000}
        ], [product]
    )

    if (product.inventory.length === 0) {
        return null
    }

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

`
