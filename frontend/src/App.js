import './App.css';
import styled from 'styled-components/macro';
import AppHeader from "./components/AppHeader";
import ShowProductsInTable from "./components/ProductTable";

function App() {
    return (
        <PageLayout>
            <AppHeader/>
            <ShowProductsInTable/>
        </PageLayout>
    );
}

export default App;

const PageLayout = styled.div`
  display: grid;
  height: 100vh;
  grid-template-rows: auto 1fr;
  margin: 0;
  padding: 0;

  background: #E9E8E8;`


