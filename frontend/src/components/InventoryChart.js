import {Chart} from 'react-charts'
import React from 'react'
import styled from 'styled-components/macro';

export default function InventoryChart({product}) {


    const data = React.useMemo(
        () => [
            {
                label: 'Series 1',
                data: product.inventory.map((item, index) => ({primary: index+1, secondary: item.amount}))
            }],[]
    )

    const axes = React.useMemo(
        () => [
            {primary: true, type: "ordinal", position: "bottom"},
            {position: "left", type: "linear", stacked: false}
        ], []
    )

    return (
        <Wrapper>
            <Chart data={data} axes={axes} tooltip />
        </Wrapper>
    )

}

const Wrapper = styled.div`
height: 200px;
width: 200px;
`




