import styled from 'styled-components/macro';

export default function AppHeader() {

    return (
        <Header>
            <h1>Inventory Management</h1>
        </Header>
    )
}

const Header = styled.header`
  background-color: orange;
  
  h1 {
    border-width: 4px;
    padding: 8px;
    text-align: center;
    color: black;
  }`