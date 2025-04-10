
import { Route, Routes } from 'react-router-dom'
import './App.css'
import AddFood from './pages/AddFood/AddFood'
import ListFood from './pages/ListFood/ListFood'
import Orders from './pages/Orders/Orders'
import SideBar from './components/SideBar/SideBar'
import MenuBar from './components/MenuBar/MenuBar'
import { useState } from 'react'

function App() {
  const [sideBarVisible, setSideBarVisible] = useState(true);

  const toggleSideBar = () => {
    setSideBarVisible(!sideBarVisible);
  };

  return (
    <div className="d-flex" id="wrapper">
            
            <SideBar sideBarVisible={sideBarVisible} />
            
            <div id="page-content-wrapper">
                <MenuBar toggleSideBar={toggleSideBar} />
                
                
                <div className="container-fluid">
                    <Routes>
                        <Route path="/add" element={<AddFood/>} />
                        <Route path="/orders" element={<Orders/>} />
                        <Route path="/list" element={<ListFood/>} />
                        <Route path="/" element={<ListFood/>} />
                    </Routes>
                </div>
            </div>
        </div>
  )
}

export default App
