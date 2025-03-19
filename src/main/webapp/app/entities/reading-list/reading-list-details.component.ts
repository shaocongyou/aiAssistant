import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ReadingListService from './reading-list.service';
import { useDateFormat } from '@/shared/composables';
import { type IReadingList } from '@/shared/model/reading-list.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ReadingListDetails',
  setup() {
    const dateFormat = useDateFormat();
    const readingListService = inject('readingListService', () => new ReadingListService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const readingList: Ref<IReadingList> = ref({});

    const retrieveReadingList = async readingListId => {
      try {
        const res = await readingListService().find(readingListId);
        readingList.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.readingListId) {
      retrieveReadingList(route.params.readingListId);
    }

    return {
      ...dateFormat,
      alertService,
      readingList,

      previousState,
    };
  },
});
