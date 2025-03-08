import GridLayout from 'react-grid-layout';
import { useState, useEffect } from 'react';
import 'react-grid-layout/css/styles.css';
import 'react-resizable/css/styles.css';
import Chart from '../components/KPI/Chart';
import { BsPencilSquare } from 'react-icons/bs';

export default function DashBoard() {

  const [isEditable, setIsEditable] = useState(true);

  const [layout, setLayout] = useState(
    JSON.parse(localStorage.getItem('dashboardLayout')) || [
      { i: '1', x: 0, y: 0, w: 2, h: 2,  static: true },
      { i: '2', x: 2, y: 0, w: 2, h: 2, static: true },
    ],
  );

  useEffect(() => {
    localStorage.setItem('dashboardLayout', JSON.stringify(layout));
  }, [layout]);

  const onLayoutChange = (newLayout) => {
    setLayout(newLayout);
  };

  const data = [
    { name: 'Jan', uv: 400, pv: 2400, amt: 2400 },
    { name: 'Feb', uv: 300, pv: 1398, amt: 2210 },
    { name: 'Mar', uv: 200, pv: 9800, amt: 2290 },
    { name: 'Apr', uv: 278, pv: 3908, amt: 2000 },
    { name: 'May', uv: 189, pv: 4800, amt: 2181 },
    { name: 'Jun', uv: 239, pv: 3800, amt: 2500 },
    { name: 'Jul', uv: 349, pv: 4300, amt: 2100 },
  ];

  return (
    <div className="dashboard">
      <div className='dashboard-header'>
        <h1>Dashboard</h1>
        <button className="edit-button" onClick={() => setIsEditable(!isEditable)}>
          <BsPencilSquare className="edit-icon"/>
        </button>
      </div>
      <div className="border-container">
        <GridLayout
          className="layout"
          layout={layout}
          cols={6}
          rowHeight={150}
          width={1370}
          margin={[10, 10]}
          isResizable={false}
          draggableHandle=".handle"
          onLayoutChange={onLayoutChange}
        >
          <div key="1" className="widget handle">
            <Chart data={data} title={'Fake Graph'} type={'line'}/>
          </div>
          <div key="2" className="widget handle">
            <Chart data={data} title={'Fake Marph'} type={'bar'}/>

          </div>
          <div key="3" className="widget">
            <div className="handle">Drag Me</div>
            Widget 3
          </div>
        </GridLayout>
      </div>
    </div>
  );
}