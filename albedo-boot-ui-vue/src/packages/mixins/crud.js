import {
    findByvalue,
    getComponent,
    getSearchType,
    setDic,
    setPx
} from "../utils/util.js";
import {
    validateNull
} from '../utils/validate.js'
import crudInput from "../crud/src/crud-input";
import crudSelect from "../crud/src/crud-select";
import crudRadio from "../crud/src/crud-radio";
import crudCheckbox from "../crud/src/crud-checkbox";
import crudCascader from "../crud/src/crud-cascader";
import crudDate from "../crud/src/crud-date";
import crudTime from "../crud/src/crud-time";
import crudInputNumber from '../crud/src/crud-input-number';
import crudUeditor from '../crud/src/crud-ueditor';
import crudSwitch from '../crud/src/crud-switch';
export default function() {
    return {
        props: {
            option: {
                type: Object,
                required: true,
                default: {}
            },
        },
        components: {
            crudInput,
            crudSelect,
            crudRadio,
            crudCheckbox,
            crudDate,
            crudTime,
            crudCascader,
            crudInputNumber,
            crudUeditor,
            crudSwitch
        },
        methods: {
            GetDic: function(list) {
                return new Promise((resolve, reject) => {
                    let result = [];
                    if (validateNull(list)) {
                        return;
                    }
                    list.forEach(ele => {
                        result.push(new Promise((resolve, reject) => {
                            if (validateNull(this.option.dicUrl)) {
                                resolve(this.option.dicData[ele]);
                            } else {
                                this.$http.get(`${this.option.dicUrl}/${ele}`).then(function(response) {
                                    resolve(validateNull(response.data.data) ? [] : response.data.data);
                                })
                            }
                        }))
                    })
                    let value = {};
                    Promise.all(result).then(data => {
                        list.forEach((ele, index) => {
                            value[ele] = data[index];
                        })
                        resolve(value);
                    })
                });

            },
            getComponent: function(type) {
                return getComponent(type);
            },
            getSearchType: function(type) {
                return getSearchType(type);
            },
            findByvalue: function(dic, val) {
                return findByvalue(dic, val);
            },
            setDic: function(dicData, DIC) {
                return setDic(dicData, DIC);
            },
            setPx: function(val, defval) {
                return setPx(val, defval);
            },
        }
    };
};
