import './App.css';
import styled from 'styled-components/macro';
import ProductTable from "./components/ProductTable";
import AppHeader from "./components/AppHeader";

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
  height: 100vh;
  display: grid;
  grid-template-rows: auto 1fr;
  align-content: center;

  background: #eee;`


