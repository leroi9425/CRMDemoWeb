import { useState, useEffect } from "react";
import { getCustomers, createCustomer, updateCustomer, deleteCustomer, getCustomersPage, getCustomerDetail, filterCustomersPage, exportCustomer, getExportTemplates, saveExportTemplate } from "../api/customerApi";
import { useAuth } from "../context/AuthContext";
import ExcelInOutPut from "./ExcelInOutPut";

export default function ExportExcel() {
    return
    <div className={`fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-50 flex items-center justify-center transition-opacity duration-300 ${isExportModalOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`}>
            <div className={`bg-white rounded-2xl shadow-xl w-full max-w-lg mx-4 overflow-hidden flex flex-col max-h-[90vh] transition-transform duration-300 ${isExportModalOpen ? 'scale-100' : 'scale-95'}`}>
                <div className="px-6 py-4 border-b border-slate-200 flex justify-between items-center bg-slate-50">
                    <h3 className="text-lg font-semibold text-slate-900"><i className="fa-solid fa-file-excel text-emerald-600 mr-2"></i>Xuất file Excel</h3>
                    <button onClick={closeExportModal} className="text-slate-400 hover:text-slate-600 transition-colors p-1 rounded-md hover:bg-slate-200">
                        <i className="fa-solid fa-xmark text-xl"></i>
                    </button>
                </div>
                <div className="px-6 py-4 overflow-y-auto">
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-slate-700 mb-2">Chọn mẫu Export</label>
                        <select 
                            value={selectedTemplateId} 
                            onChange={(e) => setSelectedTemplateId(e.target.value)} 
                            className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary bg-white"
                        >
                            <option value="new">-- Tạo mẫu mới --</option>
                            {exportTemplates.map(t => (
                                <option key={t.id} value={t.id}>{t.name}</option>
                            ))}
                        </select>
                    </div>
                    
                    {selectedTemplateId === "new" ? (
                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-2">Chọn các trường muốn export:</label>
                            <div className="grid grid-cols-2 gap-3 bg-slate-50 p-4 rounded-lg border border-slate-200">
                                {ALL_EXPORT_FIELDS.map(field => (
                                    <label key={field.value} className="flex items-center space-x-2 cursor-pointer hover:bg-slate-100 p-1 rounded transition-colors">
                                        <input 
                                            type="checkbox" 
                                            checked={selectedFields.includes(field.value)} 
                                            onChange={() => handleFieldToggle(field.value)}
                                            className="rounded border-slate-300 text-primary focus:ring-primary"
                                        />
                                        <span className="text-sm text-slate-700">{field.label}</span>
                                    </label>
                                ))}
                            </div>
                        </div>
                    ) : (
                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-2">Các trường trong mẫu:</label>
                            <div className="flex flex-wrap gap-2 bg-slate-50 p-4 rounded-lg border border-slate-200">
                                {exportTemplates.find(t => t.id.toString() === selectedTemplateId.toString())?.fields.map(f => {
                                    const fieldInfo = ALL_EXPORT_FIELDS.find(af => af.value === f);
                                    return (
                                        <span key={f} className="px-3 py-1 bg-white border border-slate-300 text-slate-600 text-sm rounded-full shadow-sm">
                                            <i className="fa-solid fa-check text-emerald-500 mr-1.5"></i>
                                            {fieldInfo ? fieldInfo.label : f}
                                        </span>
                                    );
                                })}
                            </div>
                        </div>
                    )}
                </div>
                <div className="px-6 py-4 border-t border-slate-200 bg-slate-50 flex justify-end space-x-3">
                    <button onClick={closeExportModal} className="px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors">Hủy</button>
                    <button onClick={handleExportClick} className="px-4 py-2 text-sm font-medium text-white bg-emerald-600 rounded-lg hover:bg-emerald-700 transition-colors shadow-sm flex items-center">
                        <i className="fa-solid fa-download mr-2"></i> Xuất Export
                    </button>
                </div>
            </div>
        </div>

        {/* Save Template Confirm Modal */}
        <div className={`fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-50 flex items-center justify-center transition-opacity duration-300 ${isSaveTemplateModalOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`}>
            <div className={`bg-white rounded-2xl shadow-xl w-full max-w-sm mx-4 overflow-hidden transition-transform duration-300 ${isSaveTemplateModalOpen ? 'scale-100' : 'scale-95'}`}>
                <div className="p-6">
                    <div className="w-12 h-12 rounded-full bg-blue-100 flex items-center justify-center mb-4 text-primary">
                        <i className="fa-solid fa-floppy-disk text-2xl"></i>
                    </div>
                    <h3 className="text-lg font-bold text-slate-900 mb-2">Lưu mẫu xuất Excel?</h3>
                    <p className="text-slate-500 text-sm mb-4">Bạn có muốn lưu các trường đã chọn thành một mẫu để sử dụng cho lần sau không?</p>
                    
                    <input 
                        type="text" 
                        placeholder="Nhập tên mẫu (VD: Mẫu báo cáo sếp)..." 
                        value={newTemplateName}
                        onChange={(e) => setNewTemplateName(e.target.value)}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm mb-6 focus:outline-none focus:ring-2 focus:ring-primary"
                    />
                    
                    <div className="flex flex-col space-y-2">
                        <button onClick={confirmSaveTemplateAndExport} className="px-4 py-2 text-sm font-medium text-white bg-primary rounded-lg hover:bg-blue-600 w-full transition-colors">
                            Lưu mẫu và Xuất file
                        </button>
                        <button onClick={skipSaveTemplateAndExport} className="px-4 py-2 text-sm font-medium text-emerald-600 bg-emerald-50 rounded-lg hover:bg-emerald-100 w-full transition-colors">
                            Không lưu, chỉ Xuất file
                        </button>
                        <button onClick={() => setIsSaveTemplateModalOpen(false)} className="px-4 py-2 text-sm font-medium text-slate-500 hover:text-slate-700 w-full mt-2">
                            Hủy bỏ
                        </button>
                    </div>
                </div>
            </div>
        </div>
}
            