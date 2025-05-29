import React from 'react';
import './App.css';
import Home from './pages/Home.tsx';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Metrics from './pages/Metrics.tsx';
import WebSocketClient from './components/WebSocketClient.tsx';

function App() {
    return (
        <div className="app">
            <WebSocketClient />
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
