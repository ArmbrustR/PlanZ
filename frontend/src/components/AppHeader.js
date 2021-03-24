import styled from 'styled-components/macro';

export default function AppHeader() {


    return (
        <Header>
            <h1>Inventory Management</h1>
        </Header>
    )
}

const Header = styled.header`
  h1 {
    padding: 8px;
    text-align: center;
    font-size: 2em;
    color: black;
  }`