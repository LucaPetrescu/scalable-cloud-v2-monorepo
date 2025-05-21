import React from 'react';
import './App.css';
import Home from './pages/Home.tsx';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { RouteSelect } from './components/RouteSelect/RouteSelect.tsx';
import Metrics from './pages/Metrics.tsx';

function App() {
    return (
        <div className="app">
            <BrowserRouter>
                <div className="text-stone-950 bg-stone-100">
                    <Routes>
                        <Route path="/" element={<Home />} />
                        <Route path="/metrics/:service/:metricType" element={<Metrics />} />
                    </Routes>
                </div>
            </BrowserRouter>
        </div>
    );
}

export default App;
