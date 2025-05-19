import React from 'react';
import './App.css';
import Home from './pages/Home.tsx';

function App() {
  return (
    <div className="app">
      <div className={`text-stone-950 bg-stone-100`}>
        <Home />
      </div>
    </div>
  );
}

export default App;