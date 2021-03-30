import './App.css';
import styled from 'styled-components/macro';
import AppHeader from "./components/AppHeader";
import ProductTable from "./components/ProductTable";

function App() {
  return (
      <PageLayout>
        <AppHeader/>
        <ProductTable/>
      </PageLayout>
  );
}

export default App;

const PageLayout = styled.div`
  display: grid;
  grid-template-rows: auto 1fr;
  align-content: center;
  margin: 0;
  padding: 0;

  background: #E9E8E8;`


