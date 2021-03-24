import * as React from "react";
import {useTable} from 'react-table'
import styled from 'styled-components'

export default function () {


        const data = React.useMemo(() =>
                [
                    {
                        image: "abc",
                        details: 'ASIN, TITLE, DESCRIPTION',
                        salesTrend: 'ARROW UP / DOWN',
                        inventoryAlert: 'GREEN / YELLOW / RED',
                        expectedSales: 'ARROW UP / DOWN',
                    },
                    {
                        image: "abc",
                        details: 'SECOND ITEM',
                        salesTrend: 'ARROW UP / DOWN',
                        inventoryAlert: 'GREEN / YELLOW / RED',
                        expectedSales: 'ARROW UP / DOWN',
                    }
                ],
            []
        )

    const columns = React.useMemo(
            () => [
                {
                    Header: 'Image',
                    accessor: 'image',
                    Cell: (row) => { return <div><img width={50} height={50} src="https://m.media-amazon.com/images/I/71Mwew4YF5L._AC_UL320_.jpg"/></div> },
                    id: "image"
                },
                {
                    Header: 'Details',
                    accessor: 'details',
                },
                {
                    Header: 'Sales Trend',
                    accessor: "salesTrend",
                },
                {
                    Header: 'Inventory altert',
                    accessor: 'inventoryAlert',
                },
                {
                    Header: 'Expected sales',
                    accessor: 'expectedSales',
                },
            ],
            []
        )


        const {
            getTableProps,
            getTableBodyProps,
            headerGroups,
            rows,
            prepareRow,
        } = useTable({columns, data})

        return (
            <Styles>
                <table {...getTableProps()}>
                    <thead>
                    {headerGroups.map(headerGroup => (
                        <tr {...headerGroup.getHeaderGroupProps()}>
                            {headerGroup.headers.map(column => (
                                <th {...column.getHeaderProps()}>{column.render('Header')}</th>
                            ))}
                        </tr>
                    ))}
                    </thead>
                    <tbody {...getTableBodyProps()}>
                    {rows.map(row => {
                        prepareRow(row)
                        return (
                            <tr {...row.getRowProps()}>
                                {row.cells.map(cell => {
                                    return <td {...cell.getCellProps()}>{cell.render('Cell')}</td>
                                })}
                            </tr>
                        )
                    })}
                    </tbody>
                </table>
            </Styles>
        )
    }

    const Styles = styled.div`
      display: grid;
      grid-template-rows: 1fr;
      align-content: center;
      padding: 8px;

      table {
        min-width: 300px;
        border-spacing: 0;
        border: 1px solid black;

        tr {
          :last-child {
            td {
              border-bottom: 0;
            }
          }
        }

        th, td {
          padding: 0.5rem;
          border-bottom: 1px solid black;
          border-right: 1px solid black;
        }
      }`