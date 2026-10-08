import { useState, useEffect } from "react";
import { exportCustomer, getExportTemplates, saveExportTemplate } from "../api/customerApi";

const ALL_EXPORT_FIELDS = [
    { value: "name", label: "Tên khách hàng" },
    { value: "email", label: "Email" },
    { value: "phoneNumber", label: "Số điện thoại" },
    { value: "dateOfBirth", label: "Ngày sinh" },
    { value: "location", label: "Địa chỉ" },
    { value: "gender", label: "Giới tính" },
    { value: "customerCode", label: "Mã khách" },
    { value: "company", label: "Công ty" },
    { value: "contacts", label: "Liên hệ" }
];

export default function ExportExcelModal({ isOpen, onClose, filterData, showToast }) {
    const [isSaveTemplateModalOpen, setIsSaveTemplateModalOpen] = useState(false);
    const [exportTemplates, setExportTemplates] = useState([]);
    const [selectedTemplateId, setSelectedTemplateId] = useState("");
    const [selectedFields, setSelectedFields] = useState([]);
    const [newTemplateName, setNewTemplateName] = useState("");
    const [isCreateMode, setIsCreateMode] = useState(false);

    useEffect(() => {
        if (isOpen) {
            fetchExportTemplates();
            setIsCreateMode(false);
            setSelectedFields([]);
            setNewTemplateName("");
            setIsSaveTemplateModalOpen(false);
        }
    }, [isOpen]);

    const fetchExportTemplates = async () => {
        try {
            const res = await getExportTemplates();
            const templates = (res.data || []).map(t => ({
                ...t,
                fields: typeof t.fields === 'string' ? t.fields.split(',') : t.fields
            }));
            setExportTemplates(templates);
            if (templates.length > 0) {
                setSelectedTemplateId(templates[0].id.toString());
            }
        } catch (error) {
            console.error("Lỗi tải mẫu export:", error);
        }
    };

    const handleFieldToggle = (value) => {
        if (selectedFields.includes(value)) {
            setSelectedFields(selectedFields.filter(f => f !== value));
        } else {
            setSelectedFields([...selectedFields, value]);
        }
    };

    const executeExport = async (fields) => {
        try {
            if (!fields || fields.length === 0) {
                showToast("Vui lòng chọn ít nhất 1 trường để export", "danger");
                return;
            }
            
            showToast("Đang xử lý xuất file...", "success");
            const payload = {
                ...filterData,
                gender: filterData.gender === "" ? null : filterData.gender === "true",
                fromDateOfBirth: filterData.fromDateOfBirth || null,
                toDateOfBirth: filterData.toDateOfBirth || null,
            };

            const res = await exportCustomer(payload, fields);

            const url = window.URL.createObjectURL(new Blob([res.data]));
            const link = document.createElement("a");
            link.href = url;
            link.download = "customer.xlsx";
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);
            
            setIsSaveTemplateModalOpen(false);
            onClose();
        } catch (error) {
            console.error("export loi: ", error);
            showToast("Lỗi khi xuất file", "danger");
        }
    };

    const handleExportClick = () => {
        if (isCreateMode) {
            if (selectedFields.length === 0) {
                showToast("Vui lòng chọn ít nhất 1 trường", "danger");
                return;
            }
            setIsSaveTemplateModalOpen(true);
        } else {
            if (!selectedTemplateId) {
                showToast("Vui lòng chọn mẫu", "danger");
                return;
            }
            const template = exportTemplates.find(t => t.id.toString() === selectedTemplateId.toString());
            if (template) executeExport(template.fields);
        }
    };

    const confirmSaveTemplateAndExport = async () => {
        if (!newTemplateName.trim()) {
            showToast("Vui lòng nhập tên mẫu", "danger");
            return;
        }
        try {
            await saveExportTemplate({ 
                name: newTemplateName, 
                fields: selectedFields.join(','),
                id: 0,
                userId: 0
            });
            showToast("Đã lưu mẫu thành công", "success");
            fetchExportTemplates(); 
        } catch (error) {
            console.error("Lỗi lưu mẫu:", error);
            showToast("Không thể lưu mẫu", "danger");
        }
        executeExport(selectedFields);
    };

    const skipSaveTemplateAndExport = () => {
        executeExport(selectedFields);
    };

    if (!isOpen) return null;

    return (
        <>
            {/* Export Modal */}
            <div className={`fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-50 flex items-center justify-center transition-opacity duration-300 opacity-100`}>
                <div className={`bg-white rounded-2xl shadow-xl w-full max-w-lg mx-4 overflow-hidden flex flex-col max-h-[90vh] transition-transform duration-300 scale-100`}>
                    <div className="px-6 py-4 border-b border-slate-200 flex justify-between items-center bg-slate-50">
                        <h3 className="text-lg font-semibold text-slate-900">
                            <i className="fa-solid fa-file-excel text-emerald-600 mr-2"></i>
                            {isCreateMode ? "Tạo mẫu Export mới" : "Xuất file Excel"}
                        </h3>
                        <button onClick={onClose} className="text-slate-400 hover:text-slate-600 transition-colors p-1 rounded-md hover:bg-slate-200">
                            <i className="fa-solid fa-xmark text-xl"></i>
                        </button>
                    </div>
                    <div className="px-6 py-4 overflow-y-auto min-h-[250px]">
                        {!isCreateMode ? (
                            <div className="space-y-4">
                                <div className="flex items-end space-x-3">
                                    <div className="flex-grow">
                                        <label className="block text-sm font-medium text-slate-700 mb-2">Chọn mẫu có sẵn</label>
                                        <select 
                                            value={selectedTemplateId} 
                                            onChange={(e) => setSelectedTemplateId(e.target.value)} 
                                            className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary bg-white"
                                        >
                                            {exportTemplates.length === 0 && <option value="">-- Chưa có mẫu nào --</option>}
                                            {exportTemplates.map(t => (
                                                <option key={t.id} value={t.id}>{t.name}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <button 
                                        onClick={() => setIsCreateMode(true)}
                                        className="px-4 py-2 border border-primary text-primary hover:bg-blue-50 font-medium rounded-lg transition-colors whitespace-nowrap flex items-center h-[42px]"
                                    >
                                        <i className="fa-solid fa-plus mr-2"></i> Tạo mẫu mới
                                    </button>
                                </div>
                                
                                {selectedTemplateId && exportTemplates.length > 0 && (
                                    <div className="mt-4">
                                        <label className="block text-sm font-medium text-slate-700 mb-2">Cấu hình cột sẽ xuất:</label>
                                        <div className="flex flex-nowrap items-center bg-slate-50 p-4 rounded-lg border border-slate-200 overflow-x-auto pb-4">
                                            {exportTemplates.find(t => t.id.toString() === selectedTemplateId.toString())?.fields?.map((f, index, arr) => {
                                                const fieldInfo = ALL_EXPORT_FIELDS.find(af => af.value === f);
                                                return (
                                                    <div key={f} className="flex items-center flex-shrink-0">
                                                        <span className="px-3 py-1.5 bg-white border border-slate-300 text-slate-700 text-sm rounded-md shadow-sm flex items-center font-medium">
                                                            <span className="w-5 h-5 flex items-center justify-center bg-emerald-100 text-emerald-700 rounded-full text-xs mr-2 font-bold">{index + 1}</span>
                                                            {fieldInfo ? fieldInfo.label : f}
                                                        </span>
                                                        {/* {index < arr.length - 1 && (
                                                            <i className="fa-solid fa-arrow-right-long text-slate-400 mx-3"></i>
                                                        )} */}
                                                    </div>
                                                );
                                            })}
                                        </div>
                                    </div>
                                )}
                            </div>
                        ) : (
                            <div className="space-y-4">
                                <div className="bg-blue-50 text-blue-800 p-3 rounded-lg text-sm mb-4">
                                    <i className="fa-solid fa-circle-info mr-2"></i> Tích chọn các cột dữ liệu bạn muốn xuất ra Excel.
                                </div>
                                <label className="block text-sm font-medium text-slate-700 mb-2">Chọn các trường dữ liệu:</label>
                                <div className="grid grid-cols-2 gap-3 bg-slate-50 p-4 rounded-lg border border-slate-200">
                                    {ALL_EXPORT_FIELDS.map(field => (
                                        <label key={field.value} className="flex items-center space-x-2 cursor-pointer hover:bg-slate-100 p-1.5 rounded transition-colors">
                                            <input 
                                                type="checkbox" 
                                                checked={selectedFields.includes(field.value)} 
                                                onChange={() => handleFieldToggle(field.value)}
                                                className="rounded border-slate-300 text-primary focus:ring-primary w-4 h-4"
                                            />
                                            <span className="text-sm text-slate-700 font-medium">{field.label}</span>
                                        </label>
                                    ))}
                                </div>
                            </div>
                        )}
                    </div>
                    <div className="px-6 py-4 border-t border-slate-200 bg-slate-50 flex justify-between items-center">
                        {isCreateMode ? (
                            <button onClick={() => setIsCreateMode(false)} className="px-4 py-2 text-sm font-medium text-slate-600 hover:text-slate-900 flex items-center transition-colors">
                                <i className="fa-solid fa-arrow-left mr-2"></i> Quay lại
                            </button>
                        ) : (
                            <div></div> // Spacer
                        )}
                        
                        <div className="flex space-x-3">
                            <button onClick={onClose} className="px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors">Hủy</button>
                            <button onClick={handleExportClick} className="px-4 py-2 text-sm font-medium text-white bg-emerald-600 rounded-lg hover:bg-emerald-700 transition-colors shadow-sm flex items-center">
                                <i className="fa-solid fa-download mr-2"></i> Tải xuống
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            {/* Save Template Confirm Modal */}
            {isSaveTemplateModalOpen && (
                <div className={`fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-50 flex items-center justify-center transition-opacity duration-300 opacity-100`}>
                    <div className={`bg-white rounded-2xl shadow-xl w-full max-w-sm mx-4 overflow-hidden transition-transform duration-300 scale-100`}>
                        <div className="p-6">
                            <div className="w-12 h-12 rounded-full bg-blue-100 flex items-center justify-center mb-4 text-primary">
                                <i className="fa-solid fa-floppy-disk text-2xl"></i>
                            </div>
                            <h3 className="text-lg font-bold text-slate-900 mb-2">Lưu mẫu xuất Excel?</h3>
                            <p className="text-slate-500 text-sm mb-4">Bạn có muốn lưu cấu hình này thành mẫu cho lần sau sử dụng không?</p>
                            
                            <input 
                                type="text" 
                                placeholder="Nhập tên mẫu (VD: Báo cáo sếp)..." 
                                value={newTemplateName}
                                onChange={(e) => setNewTemplateName(e.target.value)}
                                className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm mb-6 focus:outline-none focus:ring-2 focus:ring-primary"
                            />
                            
                            <div className="flex flex-col space-y-2">
                                <button onClick={confirmSaveTemplateAndExport} className="px-4 py-2 text-sm font-medium text-white bg-primary rounded-lg hover:bg-blue-600 w-full transition-colors">
                                    Lưu mẫu và Xuất
                                </button>
                                <button onClick={skipSaveTemplateAndExport} className="px-4 py-2 text-sm font-medium text-emerald-600 bg-emerald-50 rounded-lg hover:bg-emerald-100 w-full transition-colors">
                                    Không lưu, chỉ Xuất
                                </button>
                                <button onClick={() => setIsSaveTemplateModalOpen(false)} className="px-4 py-2 text-sm font-medium text-slate-500 hover:text-slate-700 w-full mt-2">
                                    Quay lại
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}
