import GridLayout from 'react-grid-layout';
import { useState, useEffect } from 'react';
import 'react-grid-layout/css/styles.css';
import 'react-resizable/css/styles.css';
import Chart from '../components/KPI/Chart';
import { BsPencilSquare, BsTrash } from 'react-icons/bs';
import { KPI_DATA } from '../api/mock_data';

export default function Dashboard() {
  const [isEditable, setIsEditable] = useState(false);
  const [selectedKpi, setSelectedKpi] = useState('');

  const data = KPI_DATA;

  // Keep track of KPIs on the dashboard
  const [kpiList, setKpiList] = useState(
    JSON.parse(localStorage.getItem('kpiList')) || data,
  );

  // Keep track of the layout
  const [layout, setLayout] = useState(
    JSON.parse(localStorage.getItem('dashboardLayout')) ||
      data.filter((kpi) => kpi.onDashboard).map((kpi, index) => ({
        i: kpi.id.toString(),
        x: (index % 6) * 2,
        y: Math.floor(index / 6) * 2,
        w: 2,
        h: 2,
        static: false,
      })),
  );

  // Save changes to localStorage
  useEffect(() => {
    localStorage.setItem('dashboardLayout', JSON.stringify(layout));
    localStorage.setItem('kpiList', JSON.stringify(kpiList));
  }, [layout, kpiList]);

  // Handle KPI selection
  const handleKpiSelect = (e) => {
    const kpiId = e.target.value;
    if (!kpiId) return;

    setKpiList((prevKpis) =>
      prevKpis.map((kpi) =>
        kpi.id.toString() === kpiId ? { ...kpi, onDashboard: true } : kpi,
      ),
    );

    const newKpi = data.find((kpi) => kpi.id.toString() === kpiId);

    // Find the first empty spot
    const occupiedPositions = layout.map((item) => ({ x: item.x, y: item.y }));
    let x = 0, y = 0;
    while (occupiedPositions.some((pos) => pos.x === x && pos.y === y)) {
      x += 2;
      if (x >= 6) {
        x = 0;
        y += 2;
      }
    }

    const newLayoutItem = {
      i: newKpi.id.toString(),
      x: x,
      y: y,
      w: 2,
      h: 2,
      static: false,
    };

    setLayout([...layout, newLayoutItem]);
    setSelectedKpi('');
  };

  // Handle KPI removal
  const handleRemoveKpi = (kpiId) => {
    setKpiList((prevKpis) =>
      prevKpis.map((kpi) =>
        kpi.id.toString() === kpiId ? { ...kpi, onDashboard: false } : kpi,
      ),
    );

    setLayout((prevLayout) =>
      prevLayout.filter((item) => item.i !== kpiId.toString()), // Ensure comparison is correct
    );
  };

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Dashboard</h1>
        <button className="edit-button" onClick={() => setIsEditable(!isEditable)}>
          <BsPencilSquare className="edit-icon" />
        </button>
        {isEditable && (
          <select className="form-select" value={selectedKpi} onChange={handleKpiSelect}>
            <option value="">Select KPI</option>
            {kpiList.filter((kpi) => !kpi.onDashboard).map((kpi) => (
              <option key={kpi.id} value={kpi.id}>
                {kpi.title}
              </option>
            ))}
          </select>
        )}
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
          isDraggable={isEditable}
          draggableHandle=".handle"
          onLayoutChange={(newLayout) => setLayout(newLayout)}
        >
          {kpiList
            .filter((kpi) => kpi.onDashboard)
            .map((kpi) => (
              <div key={kpi.id} className="widget">
                {isEditable && (
                  <button
                    className="remove-button"
                    onClick={() => handleRemoveKpi(kpi.id.toString())}
                  >
                    <BsTrash className="remove-icon" />
                  </button>
                )}
                <div className="handle">
                  <Chart data={kpi.data} title={kpi.title} type={kpi.type} />
                </div>
              </div>
            ))}
        </GridLayout>
      </div>
    </div>
  );
}
