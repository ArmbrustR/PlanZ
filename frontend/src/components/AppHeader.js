import styled from 'styled-components/macro';

export default function AppHeader() {

    return (
        <Header>
            <h1>Make more revenue by perfect inventory management</h1>
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